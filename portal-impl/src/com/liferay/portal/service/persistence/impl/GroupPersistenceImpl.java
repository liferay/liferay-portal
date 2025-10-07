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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
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
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.model.impl.GroupModelImpl;

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
 * The persistence implementation for the group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class GroupPersistenceImpl
	extends BasePersistenceImpl<Group> implements GroupPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>GroupUtil</code> to access the group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		GroupImpl.class.getName();

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
	 * Returns all the groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if (!uuid.equals(group.getUuid())) {
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

				sb.append(_SQL_SELECT_GROUP__WHERE);

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
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
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

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_First(
			String uuid, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByUuid_First(uuid, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_First(
		String uuid, OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_Last(
			String uuid, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByUuid_Last(uuid, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_Last(
		String uuid, OrderByComparator<Group> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where uuid = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByUuid_PrevAndNext(
			long groupId, String uuid,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		uuid = Objects.toString(uuid, "");

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, group, uuid, orderByComparator, true);

			array[1] = group;

			array[2] = getByUuid_PrevAndNext(
				session, group, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByUuid_PrevAndNext(
		Session session, Group group, String uuid,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Group group :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching groups
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_GROUP__WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 = "group_.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(group_.uuid IS NULL OR group_.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException {

		Group group = fetchByUUID_G(uuid, groupId);

		if (group == null) {
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

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			if (result instanceof Group) {
				Group group = (Group)result;

				if (!Objects.equals(uuid, group.getUuid()) ||
					(groupId != group.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_GROUP__WHERE);

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

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the group that was removed
	 */
	@Override
	public Group removeByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException {

		Group group = findByUUID_G(uuid, groupId);

		return remove(group);
	}

	/**
	 * Returns the number of groups where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		Group group = fetchByUUID_G(uuid, groupId);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"group_.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(group_.uuid IS NULL OR group_.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"group_.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if (!uuid.equals(group.getUuid()) ||
							(companyId != group.getCompanyId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

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
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
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

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByUuid_C_PrevAndNext(
			long groupId, String uuid, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		uuid = Objects.toString(uuid, "");

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, group, uuid, companyId, orderByComparator, true);

			array[1] = group;

			array[2] = getByUuid_C_PrevAndNext(
				session, group, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByUuid_C_PrevAndNext(
		Session session, Group group, String uuid, long companyId,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (Group group :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

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
		"group_.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(group_.uuid IS NULL OR group_.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"group_.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if (companyId != group.getCompanyId()) {
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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByCompanyId_First(
			long companyId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByCompanyId_First(companyId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByCompanyId_First(
		long companyId, OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByCompanyId(companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByCompanyId_Last(
			long companyId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByCompanyId_Last(companyId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByCompanyId_Last(
		long companyId, OrderByComparator<Group> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByCompanyId_PrevAndNext(
			long groupId, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, group, companyId, orderByComparator, true);

			array[1] = group;

			array[2] = getByCompanyId_PrevAndNext(
				session, group, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByCompanyId_PrevAndNext(
		Session session, Group group, long companyId,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (Group group :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_GROUP__WHERE);

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
		"group_.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByLiveGroupId;
	private FinderPath _finderPathWithoutPaginationFindByLiveGroupId;
	private FinderPath _finderPathCountByLiveGroupId;

	/**
	 * Returns all the groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByLiveGroupId(long liveGroupId) {
		return findByLiveGroupId(
			liveGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByLiveGroupId(long liveGroupId, int start, int end) {
		return findByLiveGroupId(liveGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByLiveGroupId(
			liveGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByLiveGroupId;
					finderArgs = new Object[] {liveGroupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByLiveGroupId;
				finderArgs = new Object[] {
					liveGroupId, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if (liveGroupId != group.getLiveGroupId()) {
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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_LIVEGROUPID_LIVEGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(liveGroupId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByLiveGroupId_First(
			long liveGroupId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByLiveGroupId_First(liveGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("liveGroupId=");
		sb.append(liveGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByLiveGroupId_First(
		long liveGroupId, OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByLiveGroupId(
			liveGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByLiveGroupId_Last(
			long liveGroupId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByLiveGroupId_Last(liveGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("liveGroupId=");
		sb.append(liveGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByLiveGroupId_Last(
		long liveGroupId, OrderByComparator<Group> orderByComparator) {

		int count = countByLiveGroupId(liveGroupId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByLiveGroupId(
			liveGroupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByLiveGroupId_PrevAndNext(
			long groupId, long liveGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByLiveGroupId_PrevAndNext(
				session, group, liveGroupId, orderByComparator, true);

			array[1] = group;

			array[2] = getByLiveGroupId_PrevAndNext(
				session, group, liveGroupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByLiveGroupId_PrevAndNext(
		Session session, Group group, long liveGroupId,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_LIVEGROUPID_LIVEGROUPID_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(liveGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where liveGroupId = &#63; from the database.
	 *
	 * @param liveGroupId the live group ID
	 */
	@Override
	public void removeByLiveGroupId(long liveGroupId) {
		for (Group group :
				findByLiveGroupId(
					liveGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByLiveGroupId(long liveGroupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByLiveGroupId;

			Object[] finderArgs = new Object[] {liveGroupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_LIVEGROUPID_LIVEGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(liveGroupId);

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

	private static final String _FINDER_COLUMN_LIVEGROUPID_LIVEGROUPID_2 =
		"group_.liveGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_C(long companyId, long classNameId) {
		return findByC_C(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return findByC_C(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_C(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(classNameId != group.getClassNameId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_First(
			companyId, classNameId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_C(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_Last(
			long companyId, long classNameId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_Last(
			companyId, classNameId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_Last(
		long companyId, long classNameId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_C(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_C(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_C_PrevAndNext(
			long groupId, long companyId, long classNameId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, group, companyId, classNameId, orderByComparator,
				true);

			array[1] = group;

			array[2] = getByC_C_PrevAndNext(
				session, group, companyId, classNameId, orderByComparator,
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

	protected Group getByC_C_PrevAndNext(
		Session session, Group group, long companyId, long classNameId,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		for (Group group :
				findByC_C(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {companyId, classNameId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

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
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"group_.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByC_P;
	private FinderPath _finderPathWithoutPaginationFindByC_P;
	private FinderPath _finderPathCountByC_P;

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_P(long companyId, long parentGroupId) {
		return findByC_P(
			companyId, parentGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end) {

		return findByC_P(companyId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_P(
			companyId, parentGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_P;
					finderArgs = new Object[] {companyId, parentGroupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_P;
				finderArgs = new Object[] {
					companyId, parentGroupId, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_First(
			long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_First(
			companyId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_First(
		long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_P(
			companyId, parentGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_Last(
			long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_Last(
			companyId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_Last(
		long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_P(companyId, parentGroupId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_P(
			companyId, parentGroupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_P_PrevAndNext(
			long groupId, long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_P_PrevAndNext(
				session, group, companyId, parentGroupId, orderByComparator,
				true);

			array[1] = group;

			array[2] = getByC_P_PrevAndNext(
				session, group, companyId, parentGroupId, orderByComparator,
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

	protected Group getByC_P_PrevAndNext(
		Session session, Group group, long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTGROUPID_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentGroupId) {
		for (Group group :
				findByC_P(
					companyId, parentGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P(long companyId, long parentGroupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_P;

			Object[] finderArgs = new Object[] {companyId, parentGroupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

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

	private static final String _FINDER_COLUMN_C_P_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_PARENTGROUPID_2 =
		"group_.parentGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByC_GK;
	private FinderPath _finderPathWithoutPaginationFindByC_GK;
	private FinderPath _finderPathFetchByC_GK;
	private FinderPath _finderPathCountByC_GK;
	private FinderPath _finderPathWithPaginationCountByC_GK;

	/**
	 * Returns all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_GK(long companyId, String[] groupKeys) {
		return findByC_GK(
			companyId, groupKeys, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end) {

		return findByC_GK(companyId, groupKeys, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_GK(
			companyId, groupKeys, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		if (groupKeys == null) {
			groupKeys = new String[0];
		}
		else if (groupKeys.length > 1) {
			for (int i = 0; i < groupKeys.length; i++) {
				groupKeys[i] = Objects.toString(groupKeys[i], "");
			}

			groupKeys = ArrayUtil.sortedUnique(groupKeys);
		}

		if (groupKeys.length == 1) {
			Group group = fetchByC_GK(companyId, groupKeys[0]);

			if (group == null) {
				return Collections.emptyList();
			}
			else {
				List<Group> list = new ArrayList<Group>(1);

				list.add(group);

				return list;
			}
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						companyId, StringUtil.merge(groupKeys)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, StringUtil.merge(groupKeys), start, end,
					orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByC_GK, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							!ArrayUtil.contains(
								groupKeys, group.getGroupKey())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_GK_COMPANYID_2);

				if (groupKeys.length > 0) {
					sb.append("(");

					for (int i = 0; i < groupKeys.length; i++) {
						String groupKey = groupKeys[i];

						if (groupKey.isEmpty()) {
							sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_3);
						}
						else {
							sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_2);
						}

						if ((i + 1) < groupKeys.length) {
							sb.append(WHERE_OR);
						}
					}

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
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					for (String groupKey : groupKeys) {
						if ((groupKey != null) && !groupKey.isEmpty()) {
							queryPos.add(groupKey);
						}
					}

					list = (List<Group>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByC_GK, finderArgs,
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
	 * Returns the group where companyId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException {

		Group group = fetchByC_GK(companyId, groupKey);

		if (group == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", groupKey=");
			sb.append(groupKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_GK(long companyId, String groupKey) {
		return fetchByC_GK(companyId, groupKey, true);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_GK(
		long companyId, String groupKey, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			groupKey = Objects.toString(groupKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, groupKey};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_GK, finderArgs, this);
			}

			if (result instanceof Group) {
				Group group = (Group)result;

				if ((companyId != group.getCompanyId()) ||
					!Objects.equals(groupKey, group.getGroupKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_GK_COMPANYID_2);

				boolean bindGroupKey = false;

				if (groupKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_3);
				}
				else {
					bindGroupKey = true;

					sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindGroupKey) {
						queryPos.add(groupKey);
					}

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_GK, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where companyId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_GK(companyId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_GK(long companyId, String groupKey) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			groupKey = Objects.toString(groupKey, "");

			FinderPath finderPath = _finderPathCountByC_GK;

			Object[] finderArgs = new Object[] {companyId, groupKey};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_GK_COMPANYID_2);

				boolean bindGroupKey = false;

				if (groupKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_3);
				}
				else {
					bindGroupKey = true;

					sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindGroupKey) {
						queryPos.add(groupKey);
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
	 * Returns the number of groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_GK(long companyId, String[] groupKeys) {
		if (groupKeys == null) {
			groupKeys = new String[0];
		}
		else if (groupKeys.length > 1) {
			for (int i = 0; i < groupKeys.length; i++) {
				groupKeys[i] = Objects.toString(groupKeys[i], "");
			}

			groupKeys = ArrayUtil.sortedUnique(groupKeys);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			Object[] finderArgs = new Object[] {
				companyId, StringUtil.merge(groupKeys)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByC_GK, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_GK_COMPANYID_2);

				if (groupKeys.length > 0) {
					sb.append("(");

					for (int i = 0; i < groupKeys.length; i++) {
						String groupKey = groupKeys[i];

						if (groupKey.isEmpty()) {
							sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_3);
						}
						else {
							sb.append(_FINDER_COLUMN_C_GK_GROUPKEY_2);
						}

						if ((i + 1) < groupKeys.length) {
							sb.append(WHERE_OR);
						}
					}

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

					queryPos.add(companyId);

					for (String groupKey : groupKeys) {
						if ((groupKey != null) && !groupKey.isEmpty()) {
							queryPos.add(groupKey);
						}
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByC_GK, finderArgs,
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

	private static final String _FINDER_COLUMN_C_GK_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_GK_GROUPKEY_2 =
		"group_.groupKey = ?";

	private static final String _FINDER_COLUMN_C_GK_GROUPKEY_3 =
		"(group_.groupKey IS NULL OR group_.groupKey = '')";

	private FinderPath _finderPathFetchByC_F;

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException {

		Group group = fetchByC_F(companyId, friendlyURL);

		if (group == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", friendlyURL=");
			sb.append(friendlyURL);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_F(long companyId, String friendlyURL) {
		return fetchByC_F(companyId, friendlyURL, true);
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_F(
		long companyId, String friendlyURL, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			friendlyURL = Objects.toString(friendlyURL, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, friendlyURL};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_F, finderArgs, this);
			}

			if (result instanceof Group) {
				Group group = (Group)result;

				if ((companyId != group.getCompanyId()) ||
					!Objects.equals(friendlyURL, group.getFriendlyURL())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_F_COMPANYID_2);

				boolean bindFriendlyURL = false;

				if (friendlyURL.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_F_FRIENDLYURL_3);
				}
				else {
					bindFriendlyURL = true;

					sb.append(_FINDER_COLUMN_C_F_FRIENDLYURL_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindFriendlyURL) {
						queryPos.add(friendlyURL);
					}

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_F, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException {

		Group group = findByC_F(companyId, friendlyURL);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_F(long companyId, String friendlyURL) {
		Group group = fetchByC_F(companyId, friendlyURL);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_F_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_F_FRIENDLYURL_2 =
		"group_.friendlyURL = ?";

	private static final String _FINDER_COLUMN_C_F_FRIENDLYURL_3 =
		"(group_.friendlyURL IS NULL OR group_.friendlyURL = '')";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_S(long companyId, boolean site) {
		return findByC_S(
			companyId, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_S(
		long companyId, boolean site, int start, int end) {

		return findByC_S(companyId, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_S(companyId, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {companyId, site};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					companyId, site, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_First(
			long companyId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_S_First(companyId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_First(
		long companyId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_S(companyId, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_Last(
			long companyId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_S_Last(companyId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_Last(
		long companyId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_S(companyId, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_S(
			companyId, site, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_S_PrevAndNext(
			long groupId, long companyId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, group, companyId, site, orderByComparator, true);

			array[1] = group;

			array[2] = getByC_S_PrevAndNext(
				session, group, companyId, site, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByC_S_PrevAndNext(
		Session session, Group group, long companyId, boolean site,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 */
	@Override
	public void removeByC_S(long companyId, boolean site) {
		for (Group group :
				findByC_S(
					companyId, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_S(long companyId, boolean site) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, site};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_SITE_2 = "group_.site = ?";

	private FinderPath _finderPathWithPaginationFindByC_A;
	private FinderPath _finderPathWithoutPaginationFindByC_A;
	private FinderPath _finderPathCountByC_A;

	/**
	 * Returns all the groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_A(long companyId, boolean active) {
		return findByC_A(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_A(
		long companyId, boolean active, int start, int end) {

		return findByC_A(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_A(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_A;
					finderArgs = new Object[] {companyId, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_A;
				finderArgs = new Object[] {
					companyId, active, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(active != group.isActive())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(active);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_A_First(companyId, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_A(
			companyId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_A_Last(
			long companyId, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_A_Last(companyId, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_A_Last(
		long companyId, boolean active,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_A(companyId, active);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_A(
			companyId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_A_PrevAndNext(
			long groupId, long companyId, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_A_PrevAndNext(
				session, group, companyId, active, orderByComparator, true);

			array[1] = group;

			array[2] = getByC_A_PrevAndNext(
				session, group, companyId, active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByC_A_PrevAndNext(
		Session session, Group group, long companyId, boolean active,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		for (Group group :
				findByC_A(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_A;

			Object[] finderArgs = new Object[] {companyId, active};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_A_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(active);

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

	private static final String _FINDER_COLUMN_C_A_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_A_ACTIVE_2 =
		"group_.active = ?";

	private FinderPath _finderPathWithPaginationFindByC_CPK;
	private FinderPath _finderPathWithoutPaginationFindByC_CPK;
	private FinderPath _finderPathCountByC_CPK;

	/**
	 * Returns all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_CPK(long classNameId, long classPK) {
		return findByC_CPK(
			classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end) {

		return findByC_CPK(classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_CPK(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CPK;
					finderArgs = new Object[] {classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CPK;
				finderArgs = new Object[] {
					classNameId, classPK, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((classNameId != group.getClassNameId()) ||
							(classPK != group.getClassPK())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_CPK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CPK_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_CPK_First(
			classNameId, classPK, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_CPK(
			classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_CPK_Last(
			long classNameId, long classPK,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_CPK_Last(
			classNameId, classPK, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_CPK_Last(
		long classNameId, long classPK,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_CPK(classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_CPK(
			classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_CPK_PrevAndNext(
			long groupId, long classNameId, long classPK,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_CPK_PrevAndNext(
				session, group, classNameId, classPK, orderByComparator, true);

			array[1] = group;

			array[2] = getByC_CPK_PrevAndNext(
				session, group, classNameId, classPK, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByC_CPK_PrevAndNext(
		Session session, Group group, long classNameId, long classPK,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_CPK_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_CPK_CLASSPK_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_CPK(long classNameId, long classPK) {
		for (Group group :
				findByC_CPK(
					classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_CPK(long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_CPK;

			Object[] finderArgs = new Object[] {classNameId, classPK};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_CPK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CPK_CLASSPK_2);

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

	private static final String _FINDER_COLUMN_C_CPK_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_CPK_CLASSPK_2 =
		"group_.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByT_A;
	private FinderPath _finderPathWithoutPaginationFindByT_A;
	private FinderPath _finderPathCountByT_A;

	/**
	 * Returns all the groups where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByT_A(int type, boolean active) {
		return findByT_A(
			type, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where type = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByT_A(int type, boolean active, int start, int end) {
		return findByT_A(type, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where type = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByT_A(
		int type, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByT_A(type, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where type = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByT_A(
		int type, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByT_A;
					finderArgs = new Object[] {type, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByT_A;
				finderArgs = new Object[] {
					type, active, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((type != group.getType()) ||
							(active != group.isActive())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_T_A_TYPE_2);

				sb.append(_FINDER_COLUMN_T_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(type);

					queryPos.add(active);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByT_A_First(
			int type, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByT_A_First(type, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByT_A_First(
		int type, boolean active, OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByT_A(type, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByT_A_Last(
			int type, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByT_A_Last(type, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByT_A_Last(
		int type, boolean active, OrderByComparator<Group> orderByComparator) {

		int count = countByT_A(type, active);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByT_A(
			type, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByT_A_PrevAndNext(
			long groupId, int type, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByT_A_PrevAndNext(
				session, group, type, active, orderByComparator, true);

			array[1] = group;

			array[2] = getByT_A_PrevAndNext(
				session, group, type, active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Group getByT_A_PrevAndNext(
		Session session, Group group, int type, boolean active,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_T_A_TYPE_2);

		sb.append(_FINDER_COLUMN_T_A_ACTIVE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(type);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where type = &#63; and active = &#63; from the database.
	 *
	 * @param type the type
	 * @param active the active
	 */
	@Override
	public void removeByT_A(int type, boolean active) {
		for (Group group :
				findByT_A(
					type, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByT_A(int type, boolean active) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByT_A;

			Object[] finderArgs = new Object[] {type, active};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_T_A_TYPE_2);

				sb.append(_FINDER_COLUMN_T_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(type);

					queryPos.add(active);

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

	private static final String _FINDER_COLUMN_T_A_TYPE_2 =
		"group_.type = ? AND ";

	private static final String _FINDER_COLUMN_T_A_ACTIVE_2 =
		"group_.active = ?";

	private FinderPath _finderPathWithPaginationFindByGtG_C_P;
	private FinderPath _finderPathWithPaginationCountByGtG_C_P;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtG_C_P;
			finderArgs = new Object[] {
				groupId, companyId, parentGroupId, start, end, orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((groupId >= group.getGroupId()) ||
							(companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_PARENTGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_First(
			long groupId, long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_P_First(
			groupId, companyId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_First(
		long groupId, long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByGtG_C_P(
			groupId, companyId, parentGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_Last(
			long groupId, long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_P_Last(
			groupId, companyId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_Last(
		long groupId, long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByGtG_C_P(groupId, companyId, parentGroupId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByGtG_C_P(
			groupId, companyId, parentGroupId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		for (Group group :
				findByGtG_C_P(
					groupId, companyId, parentGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByGtG_C_P;

			Object[] finderArgs = new Object[] {
				groupId, companyId, parentGroupId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_PARENTGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

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

	private static final String _FINDER_COLUMN_GTG_C_P_GROUPID_2 =
		"group_.groupId > ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_P_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_P_PARENTGROUPID_2 =
		"group_.parentGroupId = ?";

	private FinderPath _finderPathFetchByC_C_C;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException {

		Group group = fetchByC_C_C(companyId, classNameId, classPK);

		if (group == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_C(long companyId, long classNameId, long classPK) {
		return fetchByC_C_C(companyId, classNameId, classPK, true);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_C(
		long companyId, long classNameId, long classPK,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, classNameId, classPK};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_C_C, finderArgs, this);
			}

			if (result instanceof Group) {
				Group group = (Group)result;

				if ((companyId != group.getCompanyId()) ||
					(classNameId != group.getClassNameId()) ||
					(classPK != group.getClassPK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_C_C, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException {

		Group group = findByC_C_C(companyId, classNameId, classPK);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		Group group = fetchByC_C_C(companyId, classNameId, classPK);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_C_C_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_C_CLASSPK_2 =
		"group_.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_P;
	private FinderPath _finderPathWithoutPaginationFindByC_C_P;
	private FinderPath _finderPathCountByC_C_P;

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		return findByC_C_P(
			companyId, classNameId, parentGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C_P;
					finderArgs = new Object[] {
						companyId, classNameId, parentGroupId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C_P;
				finderArgs = new Object[] {
					companyId, classNameId, parentGroupId, start, end,
					orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(classNameId != group.getClassNameId()) ||
							(parentGroupId != group.getParentGroupId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_P_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_P_PARENTGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(parentGroupId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_P_First(
			long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_P_First(
			companyId, classNameId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_P_First(
		long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_C_P(
			companyId, classNameId, parentGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_P_Last(
			long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_P_Last(
			companyId, classNameId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_P_Last(
		long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_C_P(companyId, classNameId, parentGroupId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_C_P(
			companyId, classNameId, parentGroupId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_C_P_PrevAndNext(
			long groupId, long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_C_P_PrevAndNext(
				session, group, companyId, classNameId, parentGroupId,
				orderByComparator, true);

			array[1] = group;

			array[2] = getByC_C_P_PrevAndNext(
				session, group, companyId, classNameId, parentGroupId,
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

	protected Group getByC_C_P_PrevAndNext(
		Session session, Group group, long companyId, long classNameId,
		long parentGroupId, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_P_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_P_PARENTGROUPID_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(parentGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		for (Group group :
				findByC_C_P(
					companyId, classNameId, parentGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_C_P;

			Object[] finderArgs = new Object[] {
				companyId, classNameId, parentGroupId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_P_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_P_PARENTGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(parentGroupId);

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

	private static final String _FINDER_COLUMN_C_C_P_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_P_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_P_PARENTGROUPID_2 =
		"group_.parentGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C_S;
	private FinderPath _finderPathWithoutPaginationFindByC_C_S;
	private FinderPath _finderPathCountByC_C_S;

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site) {

		return findByC_C_S(
			companyId, classNameId, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end) {

		return findByC_C_S(companyId, classNameId, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_C_S(
			companyId, classNameId, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C_S;
					finderArgs = new Object[] {companyId, classNameId, site};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C_S;
				finderArgs = new Object[] {
					companyId, classNameId, site, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(classNameId != group.getClassNameId()) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_S_First(
			long companyId, long classNameId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_S_First(
			companyId, classNameId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_S_First(
		long companyId, long classNameId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_C_S(
			companyId, classNameId, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_S_Last(
			long companyId, long classNameId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_C_S_Last(
			companyId, classNameId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_S_Last(
		long companyId, long classNameId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_C_S(companyId, classNameId, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_C_S(
			companyId, classNameId, site, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_C_S_PrevAndNext(
			long groupId, long companyId, long classNameId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_C_S_PrevAndNext(
				session, group, companyId, classNameId, site, orderByComparator,
				true);

			array[1] = group;

			array[2] = getByC_C_S_PrevAndNext(
				session, group, companyId, classNameId, site, orderByComparator,
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

	protected Group getByC_C_S_PrevAndNext(
		Session session, Group group, long companyId, long classNameId,
		boolean site, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_C_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 */
	@Override
	public void removeByC_C_S(long companyId, long classNameId, boolean site) {
		for (Group group :
				findByC_C_S(
					companyId, classNameId, site, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_S(long companyId, long classNameId, boolean site) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_C_S;

			Object[] finderArgs = new Object[] {companyId, classNameId, site};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_S_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_C_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_S_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_S_SITE_2 = "group_.site = ?";

	private FinderPath _finderPathWithPaginationFindByC_P_S;
	private FinderPath _finderPathWithoutPaginationFindByC_P_S;
	private FinderPath _finderPathCountByC_P_S;

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		return findByC_P_S(
			companyId, parentGroupId, site, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		return findByC_P_S(companyId, parentGroupId, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_P_S(
			companyId, parentGroupId, site, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_P_S;
					finderArgs = new Object[] {companyId, parentGroupId, site};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_P_S;
				finderArgs = new Object[] {
					companyId, parentGroupId, site, start, end,
					orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId()) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_S_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_C_P_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_First(
			long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_S_First(
			companyId, parentGroupId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_First(
		long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_P_S(
			companyId, parentGroupId, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_Last(
			long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_S_Last(
			companyId, parentGroupId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_Last(
		long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_P_S(companyId, parentGroupId, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_P_S(
			companyId, parentGroupId, site, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_P_S_PrevAndNext(
			long groupId, long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_P_S_PrevAndNext(
				session, group, companyId, parentGroupId, site,
				orderByComparator, true);

			array[1] = group;

			array[2] = getByC_P_S_PrevAndNext(
				session, group, companyId, parentGroupId, site,
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

	protected Group getByC_P_S_PrevAndNext(
		Session session, Group group, long companyId, long parentGroupId,
		boolean site, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_P_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_S_PARENTGROUPID_2);

		sb.append(_FINDER_COLUMN_C_P_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentGroupId);

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	@Override
	public void removeByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		for (Group group :
				findByC_P_S(
					companyId, parentGroupId, site, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_S(long companyId, long parentGroupId, boolean site) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_P_S;

			Object[] finderArgs = new Object[] {companyId, parentGroupId, site};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_S_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_C_P_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_P_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_S_PARENTGROUPID_2 =
		"group_.parentGroupId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_S_SITE_2 = "group_.site = ?";

	private FinderPath _finderPathFetchByC_L_GK;

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_L_GK(long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = fetchByC_L_GK(companyId, liveGroupId, groupKey);

		if (group == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", liveGroupId=");
			sb.append(liveGroupId);

			sb.append(", groupKey=");
			sb.append(groupKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		return fetchByC_L_GK(companyId, liveGroupId, groupKey, true);
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			groupKey = Objects.toString(groupKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, liveGroupId, groupKey};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_L_GK, finderArgs, this);
			}

			if (result instanceof Group) {
				Group group = (Group)result;

				if ((companyId != group.getCompanyId()) ||
					(liveGroupId != group.getLiveGroupId()) ||
					!Objects.equals(groupKey, group.getGroupKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_L_GK_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_L_GK_LIVEGROUPID_2);

				boolean bindGroupKey = false;

				if (groupKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_L_GK_GROUPKEY_3);
				}
				else {
					bindGroupKey = true;

					sb.append(_FINDER_COLUMN_C_L_GK_GROUPKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(liveGroupId);

					if (bindGroupKey) {
						queryPos.add(groupKey);
					}

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_L_GK, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_L_GK(
			long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_L_GK(companyId, liveGroupId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		Group group = fetchByC_L_GK(companyId, liveGroupId, groupKey);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_L_GK_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_L_GK_LIVEGROUPID_2 =
		"group_.liveGroupId = ? AND ";

	private static final String _FINDER_COLUMN_C_L_GK_GROUPKEY_2 =
		"group_.groupKey = ?";

	private static final String _FINDER_COLUMN_C_L_GK_GROUPKEY_3 =
		"(group_.groupKey IS NULL OR group_.groupKey = '')";

	private FinderPath _finderPathWithPaginationFindByC_LikeT_S;
	private FinderPath _finderPathWithPaginationCountByC_LikeT_S;

	/**
	 * Returns all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		return findByC_LikeT_S(
			companyId, treePath, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end) {

		return findByC_LikeT_S(companyId, treePath, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_LikeT_S(
			companyId, treePath, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LikeT_S;
			finderArgs = new Object[] {
				companyId, treePath, site, start, end, orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							!StringUtil.wildcardMatches(
								group.getTreePath(), treePath, '_', '%', '\\',
								true) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_LIKET_S_COMPANYID_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_C_LIKET_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeT_S_First(
			long companyId, String treePath, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_LikeT_S_First(
			companyId, treePath, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeT_S_First(
		long companyId, String treePath, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_LikeT_S(
			companyId, treePath, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeT_S_Last(
			long companyId, String treePath, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_LikeT_S_Last(
			companyId, treePath, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", treePathLIKE");
		sb.append(treePath);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeT_S_Last(
		long companyId, String treePath, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_LikeT_S(companyId, treePath, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_LikeT_S(
			companyId, treePath, site, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_LikeT_S_PrevAndNext(
			long groupId, long companyId, String treePath, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		treePath = Objects.toString(treePath, "");

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_LikeT_S_PrevAndNext(
				session, group, companyId, treePath, site, orderByComparator,
				true);

			array[1] = group;

			array[2] = getByC_LikeT_S_PrevAndNext(
				session, group, companyId, treePath, site, orderByComparator,
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

	protected Group getByC_LikeT_S_PrevAndNext(
		Session session, Group group, long companyId, String treePath,
		boolean site, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_LIKET_S_COMPANYID_2);

		boolean bindTreePath = false;

		if (treePath.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_3);
		}
		else {
			bindTreePath = true;

			sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_2);
		}

		sb.append(_FINDER_COLUMN_C_LIKET_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindTreePath) {
			queryPos.add(treePath);
		}

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 */
	@Override
	public void removeByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		for (Group group :
				findByC_LikeT_S(
					companyId, treePath, site, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_LikeT_S(long companyId, String treePath, boolean site) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			treePath = Objects.toString(treePath, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_LikeT_S;

			Object[] finderArgs = new Object[] {companyId, treePath, site};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_LIKET_S_COMPANYID_2);

				boolean bindTreePath = false;

				if (treePath.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_3);
				}
				else {
					bindTreePath = true;

					sb.append(_FINDER_COLUMN_C_LIKET_S_TREEPATH_2);
				}

				sb.append(_FINDER_COLUMN_C_LIKET_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindTreePath) {
						queryPos.add(treePath);
					}

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_LIKET_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKET_S_TREEPATH_2 =
		"group_.treePath LIKE ? AND ";

	private static final String _FINDER_COLUMN_C_LIKET_S_TREEPATH_3 =
		"(group_.treePath IS NULL OR group_.treePath LIKE '') AND ";

	private static final String _FINDER_COLUMN_C_LIKET_S_SITE_2 =
		"group_.site = ?";

	private FinderPath _finderPathWithPaginationFindByC_LikeN_S;
	private FinderPath _finderPathWithPaginationCountByC_LikeN_S;

	/**
	 * Returns all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site) {

		return findByC_LikeN_S(
			companyId, name, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end) {

		return findByC_LikeN_S(companyId, name, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_LikeN_S(
			companyId, name, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LikeN_S;
			finderArgs = new Object[] {
				companyId, name, site, start, end, orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							!StringUtil.wildcardMatches(
								group.getName(), name, '_', '%', '\\', false) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEN_S_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_LIKEN_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeN_S_First(
			long companyId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_LikeN_S_First(
			companyId, name, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeN_S_First(
		long companyId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_LikeN_S(
			companyId, name, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeN_S_Last(
			long companyId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_LikeN_S_Last(
			companyId, name, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeN_S_Last(
		long companyId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_LikeN_S(companyId, name, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_LikeN_S(
			companyId, name, site, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_LikeN_S_PrevAndNext(
			long groupId, long companyId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		name = Objects.toString(name, "");

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_LikeN_S_PrevAndNext(
				session, group, companyId, name, site, orderByComparator, true);

			array[1] = group;

			array[2] = getByC_LikeN_S_PrevAndNext(
				session, group, companyId, name, site, orderByComparator,
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

	protected Group getByC_LikeN_S_PrevAndNext(
		Session session, Group group, long companyId, String name, boolean site,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEN_S_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_LIKEN_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
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

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 */
	@Override
	public void removeByC_LikeN_S(long companyId, String name, boolean site) {
		for (Group group :
				findByC_LikeN_S(
					companyId, name, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_LikeN_S(long companyId, String name, boolean site) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_LikeN_S;

			Object[] finderArgs = new Object[] {companyId, name, site};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEN_S_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_LIKEN_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_LIKEN_S_SITE_2);

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

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_LIKEN_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKEN_S_NAME_2 =
		"lower(group_.name) LIKE ? AND ";

	private static final String _FINDER_COLUMN_C_LIKEN_S_NAME_3 =
		"(group_.name IS NULL OR group_.name LIKE '') AND ";

	private static final String _FINDER_COLUMN_C_LIKEN_S_SITE_2 =
		"group_.site = ?";

	private FinderPath _finderPathWithPaginationFindByC_S_A;
	private FinderPath _finderPathWithoutPaginationFindByC_S_A;
	private FinderPath _finderPathCountByC_S_A;

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_S_A(
		long companyId, boolean site, boolean active) {

		return findByC_S_A(
			companyId, site, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end) {

		return findByC_S_A(companyId, site, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_S_A(
			companyId, site, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S_A;
					finderArgs = new Object[] {companyId, site, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S_A;
				finderArgs = new Object[] {
					companyId, site, active, start, end, orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(site != group.isSite()) ||
							(active != group.isActive())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_S_A_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_A_SITE_2);

				sb.append(_FINDER_COLUMN_C_S_A_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(site);

					queryPos.add(active);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_A_First(
			long companyId, boolean site, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_S_A_First(
			companyId, site, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", site=");
		sb.append(site);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_A_First(
		long companyId, boolean site, boolean active,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_S_A(
			companyId, site, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_A_Last(
			long companyId, boolean site, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_S_A_Last(
			companyId, site, active, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", site=");
		sb.append(site);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_A_Last(
		long companyId, boolean site, boolean active,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_S_A(companyId, site, active);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_S_A(
			companyId, site, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_S_A_PrevAndNext(
			long groupId, long companyId, boolean site, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_S_A_PrevAndNext(
				session, group, companyId, site, active, orderByComparator,
				true);

			array[1] = group;

			array[2] = getByC_S_A_PrevAndNext(
				session, group, companyId, site, active, orderByComparator,
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

	protected Group getByC_S_A_PrevAndNext(
		Session session, Group group, long companyId, boolean site,
		boolean active, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_S_A_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_A_SITE_2);

		sb.append(_FINDER_COLUMN_C_S_A_ACTIVE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(site);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 */
	@Override
	public void removeByC_S_A(long companyId, boolean site, boolean active) {
		for (Group group :
				findByC_S_A(
					companyId, site, active, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_S_A(long companyId, boolean site, boolean active) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_S_A;

			Object[] finderArgs = new Object[] {companyId, site, active};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_S_A_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_A_SITE_2);

				sb.append(_FINDER_COLUMN_C_S_A_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(site);

					queryPos.add(active);

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

	private static final String _FINDER_COLUMN_C_S_A_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_A_SITE_2 =
		"group_.site = ? AND ";

	private static final String _FINDER_COLUMN_C_S_A_ACTIVE_2 =
		"group_.active = ?";

	private FinderPath _finderPathWithPaginationFindByGtG_C_C_P;
	private FinderPath _finderPathWithPaginationCountByGtG_C_C_P;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtG_C_C_P;
			finderArgs = new Object[] {
				groupId, companyId, classNameId, parentGroupId, start, end,
				orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((groupId >= group.getGroupId()) ||
							(companyId != group.getCompanyId()) ||
							(classNameId != group.getClassNameId()) ||
							(parentGroupId != group.getParentGroupId())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_PARENTGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(parentGroupId);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_C_P_First(
			long groupId, long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_C_P_First(
			groupId, companyId, classNameId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_C_P_First(
		long groupId, long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_C_P_Last(
			long groupId, long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_C_P_Last(
			groupId, companyId, classNameId, parentGroupId, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_C_P_Last(
		long groupId, long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		int count = countByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		for (Group group :
				findByGtG_C_C_P(
					groupId, companyId, classNameId, parentGroupId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByGtG_C_C_P;

			Object[] finderArgs = new Object[] {
				groupId, companyId, classNameId, parentGroupId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_GTG_C_C_P_PARENTGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(parentGroupId);

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

	private static final String _FINDER_COLUMN_GTG_C_C_P_GROUPID_2 =
		"group_.groupId > ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_C_P_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_C_P_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_C_P_PARENTGROUPID_2 =
		"group_.parentGroupId = ?";

	private FinderPath _finderPathWithPaginationFindByGtG_C_P_S;
	private FinderPath _finderPathWithPaginationCountByGtG_C_P_S;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtG_C_P_S;
			finderArgs = new Object[] {
				groupId, companyId, parentGroupId, site, start, end,
				orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((groupId >= group.getGroupId()) ||
							(companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId()) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_S_First(
			long groupId, long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_P_S_First(
			groupId, companyId, parentGroupId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_S_First(
		long groupId, long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_S_Last(
			long groupId, long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByGtG_C_P_S_Last(
			groupId, companyId, parentGroupId, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId>");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_S_Last(
		long groupId, long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByGtG_C_P_S(groupId, companyId, parentGroupId, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	@Override
	public void removeByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		for (Group group :
				findByGtG_C_P_S(
					groupId, companyId, parentGroupId, site, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByGtG_C_P_S;

			Object[] finderArgs = new Object[] {
				groupId, companyId, parentGroupId, site
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_GTG_C_P_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_GTG_C_P_S_GROUPID_2 =
		"group_.groupId > ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_P_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_P_S_PARENTGROUPID_2 =
		"group_.parentGroupId = ? AND ";

	private static final String _FINDER_COLUMN_GTG_C_P_S_SITE_2 =
		"group_.site = ?";

	private FinderPath _finderPathFetchByC_C_L_GK;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);

		if (group == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", liveGroupId=");
			sb.append(liveGroupId);

			sb.append(", groupKey=");
			sb.append(groupKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		return fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey, true);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			groupKey = Objects.toString(groupKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, classNameId, liveGroupId, groupKey
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_C_L_GK, finderArgs, this);
			}

			if (result instanceof Group) {
				Group group = (Group)result;

				if ((companyId != group.getCompanyId()) ||
					(classNameId != group.getClassNameId()) ||
					(liveGroupId != group.getLiveGroupId()) ||
					!Objects.equals(groupKey, group.getGroupKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_C_L_GK_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_L_GK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_L_GK_LIVEGROUPID_2);

				boolean bindGroupKey = false;

				if (groupKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_C_L_GK_GROUPKEY_3);
				}
				else {
					bindGroupKey = true;

					sb.append(_FINDER_COLUMN_C_C_L_GK_GROUPKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(liveGroupId);

					if (bindGroupKey) {
						queryPos.add(groupKey);
					}

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_C_L_GK, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		Group group = fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_C_L_GK_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_L_GK_CLASSNAMEID_2 =
		"group_.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_L_GK_LIVEGROUPID_2 =
		"group_.liveGroupId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_L_GK_GROUPKEY_2 =
		"group_.groupKey = ?";

	private static final String _FINDER_COLUMN_C_C_L_GK_GROUPKEY_3 =
		"(group_.groupKey IS NULL OR group_.groupKey = '')";

	private FinderPath _finderPathWithPaginationFindByC_P_LikeN_S;
	private FinderPath _finderPathWithPaginationCountByC_P_LikeN_S;

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_P_LikeN_S;
			finderArgs = new Object[] {
				companyId, parentGroupId, name, site, start, end,
				orderByComparator
			};

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId()) ||
							!StringUtil.wildcardMatches(
								group.getName(), name, '_', '%', '\\', false) ||
							(site != group.isSite())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_PARENTGROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_SITE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					queryPos.add(site);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_LikeN_S_First(
			long companyId, long parentGroupId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_LikeN_S_First(
			companyId, parentGroupId, name, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_LikeN_S_First(
		long companyId, long parentGroupId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_LikeN_S_Last(
			long companyId, long parentGroupId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_LikeN_S_Last(
			companyId, parentGroupId, name, site, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append(", site=");
		sb.append(site);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_LikeN_S_Last(
		long companyId, long parentGroupId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		int count = countByC_P_LikeN_S(companyId, parentGroupId, name, site);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_P_LikeN_S_PrevAndNext(
			long groupId, long companyId, long parentGroupId, String name,
			boolean site, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		name = Objects.toString(name, "");

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_P_LikeN_S_PrevAndNext(
				session, group, companyId, parentGroupId, name, site,
				orderByComparator, true);

			array[1] = group;

			array[2] = getByC_P_LikeN_S_PrevAndNext(
				session, group, companyId, parentGroupId, name, site,
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

	protected Group getByC_P_LikeN_S_PrevAndNext(
		Session session, Group group, long companyId, long parentGroupId,
		String name, boolean site, OrderByComparator<Group> orderByComparator,
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

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_P_LIKEN_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_LIKEN_S_PARENTGROUPID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_P_LIKEN_S_SITE_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentGroupId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		queryPos.add(site);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 */
	@Override
	public void removeByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		for (Group group :
				findByC_P_LikeN_S(
					companyId, parentGroupId, name, site, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_P_LikeN_S;

			Object[] finderArgs = new Object[] {
				companyId, parentGroupId, name, site
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_PARENTGROUPID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_P_LIKEN_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_P_LIKEN_S_SITE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					queryPos.add(site);

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

	private static final String _FINDER_COLUMN_C_P_LIKEN_S_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_LIKEN_S_PARENTGROUPID_2 =
		"group_.parentGroupId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_LIKEN_S_NAME_2 =
		"lower(group_.name) LIKE ? AND ";

	private static final String _FINDER_COLUMN_C_P_LIKEN_S_NAME_3 =
		"(group_.name IS NULL OR group_.name LIKE '') AND ";

	private static final String _FINDER_COLUMN_C_P_LIKEN_S_SITE_2 =
		"group_.site = ?";

	private FinderPath _finderPathWithPaginationFindByC_P_S_I;
	private FinderPath _finderPathWithoutPaginationFindByC_P_S_I;
	private FinderPath _finderPathCountByC_P_S_I;

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_P_S_I;
					finderArgs = new Object[] {
						companyId, parentGroupId, site, inheritContent
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_P_S_I;
				finderArgs = new Object[] {
					companyId, parentGroupId, site, inheritContent, start, end,
					orderByComparator
				};
			}

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Group group : list) {
						if ((companyId != group.getCompanyId()) ||
							(parentGroupId != group.getParentGroupId()) ||
							(site != group.isSite()) ||
							(inheritContent != group.isInheritContent())) {

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

				sb.append(_SQL_SELECT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_S_I_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_SITE_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_INHERITCONTENT_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(GroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

					queryPos.add(inheritContent);

					list = (List<Group>)QueryUtil.list(
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
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_I_First(
			long companyId, long parentGroupId, boolean site,
			boolean inheritContent, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_S_I_First(
			companyId, parentGroupId, site, inheritContent, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append(", inheritContent=");
		sb.append(inheritContent);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_I_First(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, OrderByComparator<Group> orderByComparator) {

		List<Group> list = findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_I_Last(
			long companyId, long parentGroupId, boolean site,
			boolean inheritContent, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = fetchByC_P_S_I_Last(
			companyId, parentGroupId, site, inheritContent, orderByComparator);

		if (group != null) {
			return group;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentGroupId=");
		sb.append(parentGroupId);

		sb.append(", site=");
		sb.append(site);

		sb.append(", inheritContent=");
		sb.append(inheritContent);

		sb.append("}");

		throw new NoSuchGroupException(sb.toString());
	}

	/**
	 * Returns the last group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_I_Last(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, OrderByComparator<Group> orderByComparator) {

		int count = countByC_P_S_I(
			companyId, parentGroupId, site, inheritContent);

		if (count == 0) {
			return null;
		}

		List<Group> list = findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the groups before and after the current group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param groupId the primary key of the current group
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group[] findByC_P_S_I_PrevAndNext(
			long groupId, long companyId, long parentGroupId, boolean site,
			boolean inheritContent, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		Group group = findByPrimaryKey(groupId);

		Session session = null;

		try {
			session = openSession();

			Group[] array = new GroupImpl[3];

			array[0] = getByC_P_S_I_PrevAndNext(
				session, group, companyId, parentGroupId, site, inheritContent,
				orderByComparator, true);

			array[1] = group;

			array[2] = getByC_P_S_I_PrevAndNext(
				session, group, companyId, parentGroupId, site, inheritContent,
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

	protected Group getByC_P_S_I_PrevAndNext(
		Session session, Group group, long companyId, long parentGroupId,
		boolean site, boolean inheritContent,
		OrderByComparator<Group> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_GROUP__WHERE);

		sb.append(_FINDER_COLUMN_C_P_S_I_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_S_I_PARENTGROUPID_2);

		sb.append(_FINDER_COLUMN_C_P_S_I_SITE_2);

		sb.append(_FINDER_COLUMN_C_P_S_I_INHERITCONTENT_2);

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
			sb.append(GroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentGroupId);

		queryPos.add(site);

		queryPos.add(inheritContent);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(group)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Group> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 */
	@Override
	public void removeByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		for (Group group :
				findByC_P_S_I(
					companyId, parentGroupId, site, inheritContent,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(group);
		}
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			FinderPath finderPath = _finderPathCountByC_P_S_I;

			Object[] finderArgs = new Object[] {
				companyId, parentGroupId, site, inheritContent
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_GROUP__WHERE);

				sb.append(_FINDER_COLUMN_C_P_S_I_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_PARENTGROUPID_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_SITE_2);

				sb.append(_FINDER_COLUMN_C_P_S_I_INHERITCONTENT_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentGroupId);

					queryPos.add(site);

					queryPos.add(inheritContent);

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

	private static final String _FINDER_COLUMN_C_P_S_I_COMPANYID_2 =
		"group_.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_S_I_PARENTGROUPID_2 =
		"group_.parentGroupId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_S_I_SITE_2 =
		"group_.site = ? AND ";

	private static final String _FINDER_COLUMN_C_P_S_I_INHERITCONTENT_2 =
		"group_.inheritContent = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		Group group = fetchByERC_C(externalReferenceCode, companyId);

		if (group == null) {
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

			throw new NoSuchGroupException(sb.toString());
		}

		return group;
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByERC_C(String externalReferenceCode, long companyId) {
		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			if (result instanceof Group) {
				Group group = (Group)result;

				if (!Objects.equals(
						externalReferenceCode,
						group.getExternalReferenceCode()) ||
					(companyId != group.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_GROUP__WHERE);

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

					List<Group> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						Group group = list.get(0);

						result = group;

						cacheResult(group);
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
				return (Group)result;
			}
		}
	}

	/**
	 * Removes the group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the group that was removed
	 */
	@Override
	public Group removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		Group group = findByERC_C(externalReferenceCode, companyId);

		return remove(group);
	}

	/**
	 * Returns the number of groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		Group group = fetchByERC_C(externalReferenceCode, companyId);

		if (group == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"group_.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(group_.externalReferenceCode IS NULL OR group_.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"group_.companyId = ?";

	public GroupPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Group.class);

		setModelImplClass(GroupImpl.class);
		setModelPKClass(long.class);

		setTable(GroupTable.INSTANCE);
	}

	/**
	 * Caches the group in the entity cache if it is enabled.
	 *
	 * @param group the group
	 */
	@Override
	public void cacheResult(Group group) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					group.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				GroupImpl.class, group.getPrimaryKey(), group);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {group.getUuid(), group.getGroupId()}, group);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_GK,
				new Object[] {group.getCompanyId(), group.getGroupKey()},
				group);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_F,
				new Object[] {group.getCompanyId(), group.getFriendlyURL()},
				group);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_C_C,
				new Object[] {
					group.getCompanyId(), group.getClassNameId(),
					group.getClassPK()
				},
				group);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_L_GK,
				new Object[] {
					group.getCompanyId(), group.getLiveGroupId(),
					group.getGroupKey()
				},
				group);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_C_L_GK,
				new Object[] {
					group.getCompanyId(), group.getClassNameId(),
					group.getLiveGroupId(), group.getGroupKey()
				},
				group);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					group.getExternalReferenceCode(), group.getCompanyId()
				},
				group);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the groups in the entity cache if it is enabled.
	 *
	 * @param groups the groups
	 */
	@Override
	public void cacheResult(List<Group> groups) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (groups.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (Group group : groups) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						group.getCtCollectionId())) {

				Group cachedGroup = (Group)EntityCacheUtil.getResult(
					GroupImpl.class, group.getPrimaryKey());

				if (cachedGroup == null) {
					cacheResult(group);
				}
				else {
					GroupModelImpl groupModelImpl = (GroupModelImpl)group;
					GroupModelImpl cachedGroupModelImpl =
						(GroupModelImpl)cachedGroup;

					groupModelImpl.setClassName(
						cachedGroupModelImpl.getClassName());
				}
			}
		}
	}

	/**
	 * Clears the cache for all groups.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(GroupImpl.class);

		FinderCacheUtil.clearCache(GroupImpl.class);
	}

	/**
	 * Clears the cache for the group.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Group group) {
		EntityCacheUtil.removeResult(GroupImpl.class, group);
	}

	@Override
	public void clearCache(List<Group> groups) {
		for (Group group : groups) {
			EntityCacheUtil.removeResult(GroupImpl.class, group);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(GroupImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(GroupImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(GroupModelImpl groupModelImpl) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					groupModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				groupModelImpl.getUuid(), groupModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getCompanyId(), groupModelImpl.getGroupKey()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_GK, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getCompanyId(), groupModelImpl.getFriendlyURL()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_F, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getCompanyId(), groupModelImpl.getClassNameId(),
				groupModelImpl.getClassPK()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_C_C, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getCompanyId(), groupModelImpl.getLiveGroupId(),
				groupModelImpl.getGroupKey()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_L_GK, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getCompanyId(), groupModelImpl.getClassNameId(),
				groupModelImpl.getLiveGroupId(), groupModelImpl.getGroupKey()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_C_L_GK, args, groupModelImpl);

			args = new Object[] {
				groupModelImpl.getExternalReferenceCode(),
				groupModelImpl.getCompanyId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_C, args, groupModelImpl);
		}
	}

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	@Override
	public Group create(long groupId) {
		Group group = new GroupImpl();

		group.setNew(true);
		group.setPrimaryKey(groupId);

		String uuid = PortalUUIDUtil.generate();

		group.setUuid(uuid);

		group.setCompanyId(CompanyThreadLocal.getCompanyId());

		return group;
	}

	/**
	 * Removes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group remove(long groupId) throws NoSuchGroupException {
		return remove((Serializable)groupId);
	}

	/**
	 * Removes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the group
	 * @return the group that was removed
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group remove(Serializable primaryKey) throws NoSuchGroupException {
		Session session = null;

		try {
			session = openSession();

			Group group = (Group)session.get(GroupImpl.class, primaryKey);

			if (group == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchGroupException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(group);
		}
		catch (NoSuchGroupException noSuchEntityException) {
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
	protected Group removeImpl(Group group) {
		groupToOrganizationTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToRoleTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToUserGroupTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(group)) {
				group = (Group)session.get(
					GroupImpl.class, group.getPrimaryKeyObj());
			}

			if ((group != null) && CTPersistenceHelperUtil.isRemove(group)) {
				session.delete(group);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (group != null) {
			clearCache(group);
		}

		return group;
	}

	@Override
	public Group updateImpl(Group group) {
		boolean isNew = group.isNew();

		if (!(group instanceof GroupModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(group.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(group);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in group proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Group implementation " +
					group.getClass());
		}

		GroupModelImpl groupModelImpl = (GroupModelImpl)group;

		if (Validator.isNull(group.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			group.setUuid(uuid);
		}

		if (Validator.isNull(group.getExternalReferenceCode())) {
			group.setExternalReferenceCode(group.getUuid());
		}
		else {
			if (!Objects.equals(
					groupModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					group.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = group.getCompanyId();

					long groupId = group.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = group.getPrimaryKey();
					}

					try {
						group.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Group.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								group.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Group ercGroup = fetchByERC_C(
				group.getExternalReferenceCode(), group.getCompanyId());

			if (isNew) {
				if (ercGroup != null) {
					throw new DuplicateGroupExternalReferenceCodeException(
						"Duplicate group with external reference code " +
							group.getExternalReferenceCode() + " and company " +
								group.getCompanyId());
				}
			}
			else {
				if ((ercGroup != null) &&
					(group.getGroupId() != ercGroup.getGroupId())) {

					throw new DuplicateGroupExternalReferenceCodeException(
						"Duplicate group with external reference code " +
							group.getExternalReferenceCode() + " and company " +
								group.getCompanyId());
				}
			}
		}

		if (!groupModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				group.setModifiedDate(date);
			}
			else {
				group.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(group)) {
				if (!isNew) {
					session.evict(GroupImpl.class, group.getPrimaryKeyObj());
				}

				session.save(group);
			}
			else {
				group = (Group)session.merge(group);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(GroupImpl.class, groupModelImpl, false, true);

		cacheUniqueFindersCache(groupModelImpl);

		if (isNew) {
			group.setNew(false);
		}

		group.resetOriginalValues();

		return group;
	}

	/**
	 * Returns the group with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the group
	 * @return the group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group findByPrimaryKey(Serializable primaryKey)
		throws NoSuchGroupException {

		Group group = fetchByPrimaryKey(primaryKey);

		if (group == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchGroupException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return group;
	}

	/**
	 * Returns the group with the primary key or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group findByPrimaryKey(long groupId) throws NoSuchGroupException {
		return findByPrimaryKey((Serializable)groupId);
	}

	/**
	 * Returns the group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the group
	 * @return the group, or <code>null</code> if a group with the primary key could not be found
	 */
	@Override
	public Group fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(Group.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		Group group = (Group)EntityCacheUtil.getResult(
			GroupImpl.class, primaryKey);

		if (group != null) {
			return group;
		}

		Session session = null;

		try {
			session = openSession();

			group = (Group)session.get(GroupImpl.class, primaryKey);

			if (group != null) {
				cacheResult(group);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return group;
	}

	/**
	 * Returns the group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group, or <code>null</code> if a group with the primary key could not be found
	 */
	@Override
	public Group fetchByPrimaryKey(long groupId) {
		return fetchByPrimaryKey((Serializable)groupId);
	}

	@Override
	public Map<Serializable, Group> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(Group.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Group> map = new HashMap<Serializable, Group>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Group group = fetchByPrimaryKey(primaryKey);

			if (group != null) {
				map.put(primaryKey, group);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						Group.class, primaryKey)) {

				Group group = (Group)EntityCacheUtil.getResult(
					GroupImpl.class, primaryKey);

				if (group == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, group);
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

			for (Group group : (List<Group>)query.list()) {
				map.put(group.getPrimaryKeyObj(), group);

				cacheResult(group);
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
	 * Returns all the groups.
	 *
	 * @return the groups
	 */
	@Override
	public List<Group> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of groups
	 */
	@Override
	public List<Group> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of groups
	 */
	@Override
	public List<Group> findAll(
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of groups
	 */
	@Override
	public List<Group> findAll(
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

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

			List<Group> list = null;

			if (useFinderCache) {
				list = (List<Group>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_GROUP_);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_GROUP_;

					sql = sql.concat(GroupModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<Group>)QueryUtil.list(
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
	 * Removes all the groups from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Group group : findAll()) {
			remove(group);
		}
	}

	/**
	 * Returns the number of groups.
	 *
	 * @return the number of groups
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Group.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_GROUP_);

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
	 * Returns the primaryKeys of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of organizations associated with the group
	 */
	@Override
	public long[] getOrganizationPrimaryKeys(long pk) {
		long[] pks = groupToOrganizationTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk) {

		return getOrganizations(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk, int start, int end) {

		return getOrganizations(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Organization>
			orderByComparator) {

		return groupToOrganizationTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of organizations associated with the group
	 */
	@Override
	public int getOrganizationsSize(long pk) {
		long[] pks = groupToOrganizationTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the organization is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if the organization is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOrganization(long pk, long organizationPK) {
		return groupToOrganizationTableMapper.containsTableMapping(
			pk, organizationPK);
	}

	/**
	 * Returns <code>true</code> if the group has any organizations associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with organizations
	 * @return <code>true</code> if the group has any organizations associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOrganizations(long pk) {
		if (getOrganizationsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOrganization(long pk, long organizationPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToOrganizationTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, organizationPK);
		}
		else {
			return groupToOrganizationTableMapper.addTableMapping(
				group.getCompanyId(), pk, organizationPK);
		}
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToOrganizationTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				organization.getPrimaryKey());
		}
		else {
			return groupToOrganizationTableMapper.addTableMapping(
				group.getCompanyId(), pk, organization.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOrganizations(long pk, long[] organizationPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToOrganizationTableMapper.addTableMappings(
			companyId, pk, organizationPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		return addOrganizations(
			pk,
			ListUtil.toLongArray(
				organizations,
				com.liferay.portal.kernel.model.Organization.
					ORGANIZATION_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated organizations from
	 */
	@Override
	public void clearOrganizations(long pk) {
		groupToOrganizationTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 */
	@Override
	public void removeOrganization(long pk, long organizationPK) {
		groupToOrganizationTableMapper.deleteTableMapping(pk, organizationPK);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 */
	@Override
	public void removeOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		groupToOrganizationTableMapper.deleteTableMapping(
			pk, organization.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 */
	@Override
	public void removeOrganizations(long pk, long[] organizationPKs) {
		groupToOrganizationTableMapper.deleteTableMappings(pk, organizationPKs);
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 */
	@Override
	public void removeOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		removeOrganizations(
			pk,
			ListUtil.toLongArray(
				organizations,
				com.liferay.portal.kernel.model.Organization.
					ORGANIZATION_ID_ACCESSOR));
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations to be associated with the group
	 */
	@Override
	public void setOrganizations(long pk, long[] organizationPKs) {
		Set<Long> newOrganizationPKsSet = SetUtil.fromArray(organizationPKs);
		Set<Long> oldOrganizationPKsSet = SetUtil.fromArray(
			groupToOrganizationTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeOrganizationPKsSet = new HashSet<Long>(
			oldOrganizationPKsSet);

		removeOrganizationPKsSet.removeAll(newOrganizationPKsSet);

		groupToOrganizationTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeOrganizationPKsSet));

		newOrganizationPKsSet.removeAll(oldOrganizationPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToOrganizationTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newOrganizationPKsSet));
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations to be associated with the group
	 */
	@Override
	public void setOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		try {
			long[] organizationPKs = new long[organizations.size()];

			for (int i = 0; i < organizations.size(); i++) {
				com.liferay.portal.kernel.model.Organization organization =
					organizations.get(i);

				organizationPKs[i] = organization.getPrimaryKey();
			}

			setOrganizations(pk, organizationPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of roles associated with the group
	 */
	@Override
	public long[] getRolePrimaryKeys(long pk) {
		long[] pks = groupToRoleTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(long pk) {
		return getRoles(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end) {

		return getRoles(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Role>
			orderByComparator) {

		return groupToRoleTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of roles associated with the group
	 */
	@Override
	public int getRolesSize(long pk) {
		long[] pks = groupToRoleTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the role is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if the role is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsRole(long pk, long rolePK) {
		return groupToRoleTableMapper.containsTableMapping(pk, rolePK);
	}

	/**
	 * Returns <code>true</code> if the group has any roles associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with roles
	 * @return <code>true</code> if the group has any roles associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsRoles(long pk) {
		if (getRolesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addRole(long pk, long rolePK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToRoleTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, rolePK);
		}
		else {
			return groupToRoleTableMapper.addTableMapping(
				group.getCompanyId(), pk, rolePK);
		}
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addRole(long pk, com.liferay.portal.kernel.model.Role role) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToRoleTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, role.getPrimaryKey());
		}
		else {
			return groupToRoleTableMapper.addTableMapping(
				group.getCompanyId(), pk, role.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addRoles(long pk, long[] rolePKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToRoleTableMapper.addTableMappings(
			companyId, pk, rolePKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		return addRoles(
			pk,
			ListUtil.toLongArray(
				roles, com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated roles from
	 */
	@Override
	public void clearRoles(long pk) {
		groupToRoleTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 */
	@Override
	public void removeRole(long pk, long rolePK) {
		groupToRoleTableMapper.deleteTableMapping(pk, rolePK);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 */
	@Override
	public void removeRole(long pk, com.liferay.portal.kernel.model.Role role) {
		groupToRoleTableMapper.deleteTableMapping(pk, role.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 */
	@Override
	public void removeRoles(long pk, long[] rolePKs) {
		groupToRoleTableMapper.deleteTableMappings(pk, rolePKs);
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 */
	@Override
	public void removeRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		removeRoles(
			pk,
			ListUtil.toLongArray(
				roles, com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles to be associated with the group
	 */
	@Override
	public void setRoles(long pk, long[] rolePKs) {
		Set<Long> newRolePKsSet = SetUtil.fromArray(rolePKs);
		Set<Long> oldRolePKsSet = SetUtil.fromArray(
			groupToRoleTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeRolePKsSet = new HashSet<Long>(oldRolePKsSet);

		removeRolePKsSet.removeAll(newRolePKsSet);

		groupToRoleTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeRolePKsSet));

		newRolePKsSet.removeAll(oldRolePKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToRoleTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newRolePKsSet));
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles to be associated with the group
	 */
	@Override
	public void setRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		try {
			long[] rolePKs = new long[roles.size()];

			for (int i = 0; i < roles.size(); i++) {
				com.liferay.portal.kernel.model.Role role = roles.get(i);

				rolePKs[i] = role.getPrimaryKey();
			}

			setRoles(pk, rolePKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of user groups associated with the group
	 */
	@Override
	public long[] getUserGroupPrimaryKeys(long pk) {
		long[] pks = groupToUserGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk) {

		return getUserGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end) {

		return getUserGroups(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.UserGroup>
			orderByComparator) {

		return groupToUserGroupTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of user groups associated with the group
	 */
	@Override
	public int getUserGroupsSize(long pk) {
		long[] pks = groupToUserGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user group is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if the user group is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUserGroup(long pk, long userGroupPK) {
		return groupToUserGroupTableMapper.containsTableMapping(
			pk, userGroupPK);
	}

	/**
	 * Returns <code>true</code> if the group has any user groups associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with user groups
	 * @return <code>true</code> if the group has any user groups associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUserGroups(long pk) {
		if (getUserGroupsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUserGroup(long pk, long userGroupPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userGroupPK);
		}
		else {
			return groupToUserGroupTableMapper.addTableMapping(
				group.getCompanyId(), pk, userGroupPK);
		}
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				userGroup.getPrimaryKey());
		}
		else {
			return groupToUserGroupTableMapper.addTableMapping(
				group.getCompanyId(), pk, userGroup.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUserGroups(long pk, long[] userGroupPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToUserGroupTableMapper.addTableMappings(
			companyId, pk, userGroupPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		return addUserGroups(
			pk,
			ListUtil.toLongArray(
				userGroups,
				com.liferay.portal.kernel.model.UserGroup.
					USER_GROUP_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated user groups from
	 */
	@Override
	public void clearUserGroups(long pk) {
		groupToUserGroupTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 */
	@Override
	public void removeUserGroup(long pk, long userGroupPK) {
		groupToUserGroupTableMapper.deleteTableMapping(pk, userGroupPK);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 */
	@Override
	public void removeUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		groupToUserGroupTableMapper.deleteTableMapping(
			pk, userGroup.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 */
	@Override
	public void removeUserGroups(long pk, long[] userGroupPKs) {
		groupToUserGroupTableMapper.deleteTableMappings(pk, userGroupPKs);
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 */
	@Override
	public void removeUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		removeUserGroups(
			pk,
			ListUtil.toLongArray(
				userGroups,
				com.liferay.portal.kernel.model.UserGroup.
					USER_GROUP_ID_ACCESSOR));
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups to be associated with the group
	 */
	@Override
	public void setUserGroups(long pk, long[] userGroupPKs) {
		Set<Long> newUserGroupPKsSet = SetUtil.fromArray(userGroupPKs);
		Set<Long> oldUserGroupPKsSet = SetUtil.fromArray(
			groupToUserGroupTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserGroupPKsSet = new HashSet<Long>(oldUserGroupPKsSet);

		removeUserGroupPKsSet.removeAll(newUserGroupPKsSet);

		groupToUserGroupTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserGroupPKsSet));

		newUserGroupPKsSet.removeAll(oldUserGroupPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToUserGroupTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserGroupPKsSet));
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups to be associated with the group
	 */
	@Override
	public void setUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		try {
			long[] userGroupPKs = new long[userGroups.size()];

			for (int i = 0; i < userGroups.size(); i++) {
				com.liferay.portal.kernel.model.UserGroup userGroup =
					userGroups.get(i);

				userGroupPKs[i] = userGroup.getPrimaryKey();
			}

			setUserGroups(pk, userGroupPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of users associated with the group
	 */
	@Override
	public long[] getUserPrimaryKeys(long pk) {
		long[] pks = groupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getUsers(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return groupToUserTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of users associated with the group
	 */
	@Override
	public int getUsersSize(long pk) {
		long[] pks = groupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUser(long pk, long userPK) {
		return groupToUserTableMapper.containsTableMapping(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the group has any users associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with users
	 * @return <code>true</code> if the group has any users associated with it; <code>false</code> otherwise
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
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, long userPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userPK);
		}
		else {
			return groupToUserTableMapper.addTableMapping(
				group.getCompanyId(), pk, userPK);
		}
	}

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, user.getPrimaryKey());
		}
		else {
			return groupToUserTableMapper.addTableMapping(
				group.getCompanyId(), pk, user.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(long pk, long[] userPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToUserTableMapper.addTableMappings(
			companyId, pk, userPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
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
	 * Clears all associations between the group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated users from
	 */
	@Override
	public void clearUsers(long pk) {
		groupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 */
	@Override
	public void removeUser(long pk, long userPK) {
		groupToUserTableMapper.deleteTableMapping(pk, userPK);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 */
	@Override
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user) {
		groupToUserTableMapper.deleteTableMapping(pk, user.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 */
	@Override
	public void removeUsers(long pk, long[] userPKs) {
		groupToUserTableMapper.deleteTableMappings(pk, userPKs);
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
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
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users to be associated with the group
	 */
	@Override
	public void setUsers(long pk, long[] userPKs) {
		Set<Long> newUserPKsSet = SetUtil.fromArray(userPKs);
		Set<Long> oldUserPKsSet = SetUtil.fromArray(
			groupToUserTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserPKsSet = new HashSet<Long>(oldUserPKsSet);

		removeUserPKsSet.removeAll(newUserPKsSet);

		groupToUserTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserPKsSet));

		newUserPKsSet.removeAll(oldUserPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToUserTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserPKsSet));
	}

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users to be associated with the group
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
		return "groupId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_GROUP_;
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
		return GroupModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Group_";
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
		ctStrictColumnNames.add("creatorUserId");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("parentGroupId");
		ctMergeColumnNames.add("liveGroupId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("groupKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("manualMembership");
		ctMergeColumnNames.add("membershipRestriction");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("site");
		ctMergeColumnNames.add("remoteStagingGroupCount");
		ctMergeColumnNames.add("inheritContent");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("orgs");
		ctMergeColumnNames.add("roles");
		ctMergeColumnNames.add("userGroups");
		ctMergeColumnNames.add("users");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("groupId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("Groups_Orgs");
		_mappingTableNames.add("Groups_Roles");
		_mappingTableNames.add("Groups_UserGroups");
		_mappingTableNames.add("Users_Groups");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "groupKey"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "classNameId", "classPK"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "liveGroupId", "groupKey"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"companyId", "classNameId", "liveGroupId", "groupKey"
			});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the group persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		groupToOrganizationTableMapper = TableMapperFactory.getTableMapper(
			"Groups_Orgs", "companyId", "groupId", "organizationId", this,
			organizationPersistence);

		groupToRoleTableMapper = TableMapperFactory.getTableMapper(
			"Groups_Roles", "companyId", "groupId", "roleId", this,
			rolePersistence);

		groupToUserGroupTableMapper = TableMapperFactory.getTableMapper(
			"Groups_UserGroups", "companyId", "groupId", "userGroupId", this,
			userGroupPersistence);

		groupToUserTableMapper = TableMapperFactory.getTableMapper(
			"Users_Groups", "companyId", "groupId", "userId", this,
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

		_finderPathWithPaginationFindByLiveGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLiveGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"liveGroupId"}, true);

		_finderPathWithoutPaginationFindByLiveGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLiveGroupId",
			new String[] {Long.class.getName()}, new String[] {"liveGroupId"},
			true);

		_finderPathCountByLiveGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByLiveGroupId",
			new String[] {Long.class.getName()}, new String[] {"liveGroupId"},
			false);

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

		_finderPathWithPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "parentGroupId"}, true);

		_finderPathWithoutPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentGroupId"}, true);

		_finderPathCountByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentGroupId"}, false);

		_finderPathWithPaginationFindByC_GK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_GK",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "groupKey"}, true);

		_finderPathWithoutPaginationFindByC_GK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_GK",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "groupKey"}, true);

		_finderPathFetchByC_GK = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_GK",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "groupKey"}, true);

		_finderPathCountByC_GK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_GK",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "groupKey"}, false);

		_finderPathWithPaginationCountByC_GK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_GK",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "groupKey"}, false);

		_finderPathFetchByC_F = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_F",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "friendlyURL"}, true);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "site"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "site"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "site"}, false);

		_finderPathWithPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_"}, true);

		_finderPathWithoutPaginationFindByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, true);

		_finderPathCountByC_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, false);

		_finderPathWithPaginationFindByC_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByC_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathCountByC_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathWithPaginationFindByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_A",
			new String[] {
				Integer.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"type_", "active_"}, true);

		_finderPathWithoutPaginationFindByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_A",
			new String[] {Integer.class.getName(), Boolean.class.getName()},
			new String[] {"type_", "active_"}, true);

		_finderPathCountByT_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_A",
			new String[] {Integer.class.getName(), Boolean.class.getName()},
			new String[] {"type_", "active_"}, false);

		_finderPathWithPaginationFindByGtG_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "parentGroupId"}, true);

		_finderPathWithPaginationCountByGtG_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "parentGroupId"}, false);

		_finderPathFetchByC_C_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK"}, true);

		_finderPathWithPaginationFindByC_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId", "parentGroupId"}, true);

		_finderPathWithoutPaginationFindByC_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "parentGroupId"}, true);

		_finderPathCountByC_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "parentGroupId"}, false);

		_finderPathWithPaginationFindByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId", "site"}, true);

		_finderPathWithoutPaginationFindByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "classNameId", "site"}, true);

		_finderPathCountByC_C_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "classNameId", "site"}, false);

		_finderPathWithPaginationFindByC_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "parentGroupId", "site"}, true);

		_finderPathWithoutPaginationFindByC_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "parentGroupId", "site"}, true);

		_finderPathCountByC_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "parentGroupId", "site"}, false);

		_finderPathFetchByC_L_GK = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_L_GK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "liveGroupId", "groupKey"}, true);

		_finderPathWithPaginationFindByC_LikeT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeT_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "treePath", "site"}, true);

		_finderPathWithPaginationCountByC_LikeT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeT_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "treePath", "site"}, false);

		_finderPathWithPaginationFindByC_LikeN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name", "site"}, true);

		_finderPathWithPaginationCountByC_LikeN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "name", "site"}, false);

		_finderPathWithPaginationFindByC_S_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "site", "active_"}, true);

		_finderPathWithoutPaginationFindByC_S_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "site", "active_"}, true);

		_finderPathCountByC_S_A = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_A",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "site", "active_"}, false);

		_finderPathWithPaginationFindByGtG_C_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "companyId", "classNameId", "parentGroupId"
			},
			true);

		_finderPathWithPaginationCountByGtG_C_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "companyId", "classNameId", "parentGroupId"
			},
			false);

		_finderPathWithPaginationFindByGtG_C_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "parentGroupId", "site"},
			true);

		_finderPathWithPaginationCountByGtG_C_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_P_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {"groupId", "companyId", "parentGroupId", "site"},
			false);

		_finderPathFetchByC_C_L_GK = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_C_L_GK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {
				"companyId", "classNameId", "liveGroupId", "groupKey"
			},
			true);

		_finderPathWithPaginationFindByC_P_LikeN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_LikeN_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "parentGroupId", "name", "site"}, true);

		_finderPathWithPaginationCountByC_P_LikeN_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_P_LikeN_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Boolean.class.getName()
			},
			new String[] {"companyId", "parentGroupId", "name", "site"}, false);

		_finderPathWithPaginationFindByC_P_S_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_S_I",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "parentGroupId", "site", "inheritContent"
			},
			true);

		_finderPathWithoutPaginationFindByC_P_S_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_S_I",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"companyId", "parentGroupId", "site", "inheritContent"
			},
			true);

		_finderPathCountByC_P_S_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_S_I",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"companyId", "parentGroupId", "site", "inheritContent"
			},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		GroupUtil.setPersistence(this);
	}

	public void destroy() {
		GroupUtil.setPersistence(null);

		EntityCacheUtil.removeCache(GroupImpl.class.getName());

		TableMapperFactory.removeTableMapper("Groups_Orgs");
		TableMapperFactory.removeTableMapper("Groups_Roles");
		TableMapperFactory.removeTableMapper("Groups_UserGroups");
		TableMapperFactory.removeTableMapper("Users_Groups");
	}

	@BeanReference(type = OrganizationPersistence.class)
	protected OrganizationPersistence organizationPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.Organization>
		groupToOrganizationTableMapper;

	@BeanReference(type = RolePersistence.class)
	protected RolePersistence rolePersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.Role>
		groupToRoleTableMapper;

	@BeanReference(type = UserGroupPersistence.class)
	protected UserGroupPersistence userGroupPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.UserGroup>
		groupToUserGroupTableMapper;

	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.User>
		groupToUserTableMapper;

	private static final String _SQL_SELECT_GROUP_ =
		"SELECT group_ FROM Group group_";

	private static final String _SQL_SELECT_GROUP__WHERE =
		"SELECT group_ FROM Group group_ WHERE ";

	private static final String _SQL_COUNT_GROUP_ =
		"SELECT COUNT(group_) FROM Group group_";

	private static final String _SQL_COUNT_GROUP__WHERE =
		"SELECT COUNT(group_) FROM Group group_ WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "group_.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No Group exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Group exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		GroupPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}