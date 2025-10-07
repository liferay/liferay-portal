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
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.ResourcePermissionImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the resource permission service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ResourcePermissionPersistenceImpl
	extends BasePersistenceImpl<ResourcePermission>
	implements ResourcePermissionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ResourcePermissionUtil</code> to access the resource permission persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ResourcePermissionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByName;
	private FinderPath _finderPathWithoutPaginationFindByName;
	private FinderPath _finderPathCountByName;

	/**
	 * Returns all the resource permissions where name = &#63;.
	 *
	 * @param name the name
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByName(String name) {
		return findByName(name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByName(
		String name, int start, int end) {

		return findByName(name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByName(
		String name, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByName(name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByName(
		String name, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByName;
					finderArgs = new Object[] {name};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByName;
				finderArgs = new Object[] {name, start, end, orderByComparator};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if (!name.equals(resourcePermission.getName())) {
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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_NAME_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_NAME_NAME_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindName) {
						queryPos.add(name);
					}

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByName_First(
			String name,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByName_First(
			name, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByName_First(
		String name, OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByName(
			name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByName_Last(
			String name,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByName_Last(
			name, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByName_Last(
		String name, OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByName(name);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByName(
			name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where name = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByName_PrevAndNext(
			long resourcePermissionId, String name,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		name = Objects.toString(name, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByName_PrevAndNext(
				session, resourcePermission, name, orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByName_PrevAndNext(
				session, resourcePermission, name, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ResourcePermission getByName_PrevAndNext(
		Session session, ResourcePermission resourcePermission, String name,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_NAME_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_NAME_NAME_2);
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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName(String name) {
		for (ResourcePermission resourcePermission :
				findByName(name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByName(String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByName;

			Object[] finderArgs = new Object[] {name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_NAME_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_NAME_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindName) {
						queryPos.add(name);
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

	private static final String _FINDER_COLUMN_NAME_NAME_2 =
		"resourcePermission.name = ?";

	private static final String _FINDER_COLUMN_NAME_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '')";

	private FinderPath _finderPathWithPaginationFindByScope;
	private FinderPath _finderPathWithoutPaginationFindByScope;
	private FinderPath _finderPathCountByScope;
	private FinderPath _finderPathWithPaginationCountByScope;

	/**
	 * Returns all the resource permissions where scope = &#63;.
	 *
	 * @param scope the scope
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(int scope) {
		return findByScope(scope, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(int scope, int start, int end) {
		return findByScope(scope, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(
		int scope, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByScope(scope, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(
		int scope, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByScope;
					finderArgs = new Object[] {scope};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByScope;
				finderArgs = new Object[] {
					scope, start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if (scope != resourcePermission.getScope()) {
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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_SCOPE_SCOPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(scope);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where scope = &#63;.
	 *
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByScope_First(
			int scope, OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByScope_First(
			scope, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("scope=");
		sb.append(scope);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where scope = &#63;.
	 *
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByScope_First(
		int scope, OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByScope(
			scope, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where scope = &#63;.
	 *
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByScope_Last(
			int scope, OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByScope_Last(
			scope, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("scope=");
		sb.append(scope);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where scope = &#63;.
	 *
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByScope_Last(
		int scope, OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByScope(scope);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByScope(
			scope, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where scope = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByScope_PrevAndNext(
			long resourcePermissionId, int scope,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByScope_PrevAndNext(
				session, resourcePermission, scope, orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByScope_PrevAndNext(
				session, resourcePermission, scope, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ResourcePermission getByScope_PrevAndNext(
		Session session, ResourcePermission resourcePermission, int scope,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_SCOPE_SCOPE_2);

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(scope);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the resource permissions where scope = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scopes the scopes
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(int[] scopes) {
		return findByScope(scopes, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where scope = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scopes the scopes
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(
		int[] scopes, int start, int end) {

		return findByScope(scopes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where scope = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scopes the scopes
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(
		int[] scopes, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByScope(scopes, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where scope = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param scopes the scopes
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByScope(
		int[] scopes, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		if (scopes == null) {
			scopes = new int[0];
		}
		else if (scopes.length > 1) {
			scopes = ArrayUtil.sortedUnique(scopes);
		}

		if (scopes.length == 1) {
			return findByScope(scopes[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {StringUtil.merge(scopes)};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(scopes), start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByScope, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if (!ArrayUtil.contains(
								scopes, resourcePermission.getScope())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				if (scopes.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_SCOPE_SCOPE_7);

					sb.append(StringUtil.merge(scopes));

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
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<ResourcePermission>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByScope, finderArgs,
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
	 * Removes all the resource permissions where scope = &#63; from the database.
	 *
	 * @param scope the scope
	 */
	@Override
	public void removeByScope(int scope) {
		for (ResourcePermission resourcePermission :
				findByScope(
					scope, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where scope = &#63;.
	 *
	 * @param scope the scope
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByScope(int scope) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			FinderPath finderPath = _finderPathCountByScope;

			Object[] finderArgs = new Object[] {scope};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_SCOPE_SCOPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(scope);

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
	 * Returns the number of resource permissions where scope = any &#63;.
	 *
	 * @param scopes the scopes
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByScope(int[] scopes) {
		if (scopes == null) {
			scopes = new int[0];
		}
		else if (scopes.length > 1) {
			scopes = ArrayUtil.sortedUnique(scopes);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = new Object[] {StringUtil.merge(scopes)};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByScope, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				if (scopes.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_SCOPE_SCOPE_7);

					sb.append(StringUtil.merge(scopes));

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

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByScope, finderArgs,
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

	private static final String _FINDER_COLUMN_SCOPE_SCOPE_2 =
		"resourcePermission.scope = ?";

	private static final String _FINDER_COLUMN_SCOPE_SCOPE_7 =
		"resourcePermission.scope IN (";

	private FinderPath _finderPathWithPaginationFindByRoleId;
	private FinderPath _finderPathWithoutPaginationFindByRoleId;
	private FinderPath _finderPathCountByRoleId;

	/**
	 * Returns all the resource permissions where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByRoleId(long roleId) {
		return findByRoleId(roleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByRoleId(
		long roleId, int start, int end) {

		return findByRoleId(roleId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByRoleId(
		long roleId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByRoleId(roleId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByRoleId(
		long roleId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByRoleId;
					finderArgs = new Object[] {roleId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByRoleId;
				finderArgs = new Object[] {
					roleId, start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if (roleId != resourcePermission.getRoleId()) {
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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_ROLEID_ROLEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(roleId);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByRoleId_First(
			long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByRoleId_First(
			roleId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("roleId=");
		sb.append(roleId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByRoleId_First(
		long roleId, OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByRoleId(
			roleId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByRoleId_Last(
			long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByRoleId_Last(
			roleId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("roleId=");
		sb.append(roleId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByRoleId_Last(
		long roleId, OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByRoleId(roleId);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByRoleId(
			roleId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where roleId = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByRoleId_PrevAndNext(
			long resourcePermissionId, long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByRoleId_PrevAndNext(
				session, resourcePermission, roleId, orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByRoleId_PrevAndNext(
				session, resourcePermission, roleId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ResourcePermission getByRoleId_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long roleId,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_ROLEID_ROLEID_2);

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(roleId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 */
	@Override
	public void removeByRoleId(long roleId) {
		for (ResourcePermission resourcePermission :
				findByRoleId(
					roleId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByRoleId(long roleId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			FinderPath finderPath = _finderPathCountByRoleId;

			Object[] finderArgs = new Object[] {roleId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_ROLEID_ROLEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(roleId);

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

	private static final String _FINDER_COLUMN_ROLEID_ROLEID_2 =
		"resourcePermission.roleId = ?";

	private FinderPath _finderPathWithPaginationFindByC_LikeP;
	private FinderPath _finderPathWithPaginationCountByC_LikeP;

	/**
	 * Returns all the resource permissions where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_LikeP(
		long companyId, String primKey) {

		return findByC_LikeP(
			companyId, primKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_LikeP(
		long companyId, String primKey, int start, int end) {

		return findByC_LikeP(companyId, primKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_LikeP(
		long companyId, String primKey, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_LikeP(
			companyId, primKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_LikeP(
		long companyId, String primKey, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LikeP;
			finderArgs = new Object[] {
				companyId, primKey, start, end, orderByComparator
			};

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!StringUtil.wildcardMatches(
								resourcePermission.getPrimKey(), primKey, '_',
								'%', '\\', true)) {

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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEP_COMPANYID_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindPrimKey) {
						queryPos.add(primKey);
					}

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_LikeP_First(
			long companyId, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_LikeP_First(
			companyId, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", primKeyLIKE");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_LikeP_First(
		long companyId, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_LikeP(
			companyId, primKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_LikeP_Last(
			long companyId, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_LikeP_Last(
			companyId, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", primKeyLIKE");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_LikeP_Last(
		long companyId, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_LikeP(companyId, primKey);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_LikeP(
			companyId, primKey, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_LikeP_PrevAndNext(
			long resourcePermissionId, long companyId, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		primKey = Objects.toString(primKey, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_LikeP_PrevAndNext(
				session, resourcePermission, companyId, primKey,
				orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_LikeP_PrevAndNext(
				session, resourcePermission, companyId, primKey,
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

	protected ResourcePermission getByC_LikeP_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		String primKey, OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEP_COMPANYID_2);

		boolean bindPrimKey = false;

		if (primKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_3);
		}
		else {
			bindPrimKey = true;

			sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_2);
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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindPrimKey) {
			queryPos.add(primKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where companyId = &#63; and primKey LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 */
	@Override
	public void removeByC_LikeP(long companyId, String primKey) {
		for (ResourcePermission resourcePermission :
				findByC_LikeP(
					companyId, primKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and primKey LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_LikeP(long companyId, String primKey) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_LikeP;

			Object[] finderArgs = new Object[] {companyId, primKey};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEP_COMPANYID_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_LIKEP_PRIMKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindPrimKey) {
						queryPos.add(primKey);
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

	private static final String _FINDER_COLUMN_C_LIKEP_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKEP_PRIMKEY_2 =
		"resourcePermission.primKey LIKE ?";

	private static final String _FINDER_COLUMN_C_LIKEP_PRIMKEY_3 =
		"(resourcePermission.primKey IS NULL OR resourcePermission.primKey LIKE '')";

	private FinderPath _finderPathWithPaginationFindByC_N_S;
	private FinderPath _finderPathWithoutPaginationFindByC_N_S;
	private FinderPath _finderPathCountByC_N_S;

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S(
		long companyId, String name, int scope) {

		return findByC_N_S(
			companyId, name, scope, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S(
		long companyId, String name, int scope, int start, int end) {

		return findByC_N_S(companyId, name, scope, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S(
		long companyId, String name, int scope, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S(
			companyId, name, scope, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S(
		long companyId, String name, int scope, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_N_S;
					finderArgs = new Object[] {companyId, name, scope};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_N_S;
				finderArgs = new Object[] {
					companyId, name, scope, start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope())) {

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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_SCOPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(scope);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_First(
			long companyId, String name, int scope,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_First(
			companyId, name, scope, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_First(
		long companyId, String name, int scope,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_N_S(
			companyId, name, scope, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_Last(
			long companyId, String name, int scope,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_Last(
			companyId, name, scope, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_Last(
		long companyId, String name, int scope,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_N_S(companyId, name, scope);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_N_S(
			companyId, name, scope, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_N_S_PrevAndNext(
			long resourcePermissionId, long companyId, String name, int scope,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		name = Objects.toString(name, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_N_S_PrevAndNext(
				session, resourcePermission, companyId, name, scope,
				orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_N_S_PrevAndNext(
				session, resourcePermission, companyId, name, scope,
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

	protected ResourcePermission getByC_N_S_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		String name, int scope,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_SCOPE_2);

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(scope);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 */
	@Override
	public void removeByC_N_S(long companyId, String name, int scope) {
		for (ResourcePermission resourcePermission :
				findByC_N_S(
					companyId, name, scope, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S(long companyId, String name, int scope) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByC_N_S;

			Object[] finderArgs = new Object[] {companyId, name, scope};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_SCOPE_2);

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

					queryPos.add(scope);

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

	private static final String _FINDER_COLUMN_C_N_S_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_NAME_2 =
		"resourcePermission.name = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_SCOPE_2 =
		"resourcePermission.scope = ?";

	private FinderPath _finderPathWithPaginationFindByC_S_P;
	private FinderPath _finderPathWithoutPaginationFindByC_S_P;
	private FinderPath _finderPathCountByC_S_P;

	/**
	 * Returns all the resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_S_P(
		long companyId, int scope, String primKey) {

		return findByC_S_P(
			companyId, scope, primKey, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_S_P(
		long companyId, int scope, String primKey, int start, int end) {

		return findByC_S_P(companyId, scope, primKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_S_P(
		long companyId, int scope, String primKey, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_S_P(
			companyId, scope, primKey, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_S_P(
		long companyId, int scope, String primKey, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S_P;
					finderArgs = new Object[] {companyId, scope, primKey};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S_P;
				finderArgs = new Object[] {
					companyId, scope, primKey, start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							(scope != resourcePermission.getScope()) ||
							!primKey.equals(resourcePermission.getPrimKey())) {

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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_S_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_P_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
					}

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_S_P_First(
			long companyId, int scope, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_S_P_First(
			companyId, scope, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", primKey=");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_S_P_First(
		long companyId, int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_S_P(
			companyId, scope, primKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_S_P_Last(
			long companyId, int scope, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_S_P_Last(
			companyId, scope, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", primKey=");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_S_P_Last(
		long companyId, int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_S_P(companyId, scope, primKey);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_S_P(
			companyId, scope, primKey, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_S_P_PrevAndNext(
			long resourcePermissionId, long companyId, int scope,
			String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		primKey = Objects.toString(primKey, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_S_P_PrevAndNext(
				session, resourcePermission, companyId, scope, primKey,
				orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_S_P_PrevAndNext(
				session, resourcePermission, companyId, scope, primKey,
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

	protected ResourcePermission getByC_S_P_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_S_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_P_SCOPE_2);

		boolean bindPrimKey = false;

		if (primKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_3);
		}
		else {
			bindPrimKey = true;

			sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_2);
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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(scope);

		if (bindPrimKey) {
			queryPos.add(primKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 */
	@Override
	public void removeByC_S_P(long companyId, int scope, String primKey) {
		for (ResourcePermission resourcePermission :
				findByC_S_P(
					companyId, scope, primKey, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param primKey the prim key
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_S_P(long companyId, int scope, String primKey) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = _finderPathCountByC_S_P;

			Object[] finderArgs = new Object[] {companyId, scope, primKey};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_S_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_P_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_S_P_PRIMKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
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

	private static final String _FINDER_COLUMN_C_S_P_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_P_SCOPE_2 =
		"resourcePermission.scope = ? AND ";

	private static final String _FINDER_COLUMN_C_S_P_PRIMKEY_2 =
		"resourcePermission.primKey = ?";

	private static final String _FINDER_COLUMN_C_S_P_PRIMKEY_3 =
		"(resourcePermission.primKey IS NULL OR resourcePermission.primKey = '')";

	private FinderPath _finderPathWithPaginationFindByC_N_S_P;
	private FinderPath _finderPathWithoutPaginationFindByC_N_S_P;
	private FinderPath _finderPathCountByC_N_S_P;
	private FinderPath _finderPathWithPaginationCountByC_N_S_P;

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String primKey) {

		return findByC_N_S_P(
			companyId, name, scope, primKey, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String primKey, int start,
		int end) {

		return findByC_N_S_P(companyId, name, scope, primKey, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String primKey, int start,
		int end, OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_P(
			companyId, name, scope, primKey, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String primKey, int start,
		int end, OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");
			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_N_S_P;
					finderArgs = new Object[] {companyId, name, scope, primKey};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_N_S_P;
				finderArgs = new Object[] {
					companyId, name, scope, primKey, start, end,
					orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							!primKey.equals(resourcePermission.getPrimKey())) {

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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
					}

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_P_First(
			long companyId, String name, int scope, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_P_First(
			companyId, name, scope, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", primKey=");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_P_First(
		long companyId, String name, int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_N_S_P(
			companyId, name, scope, primKey, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_P_Last(
			long companyId, String name, int scope, String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_P_Last(
			companyId, name, scope, primKey, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", primKey=");
		sb.append(primKey);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_P_Last(
		long companyId, String name, int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_N_S_P(companyId, name, scope, primKey);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_N_S_P(
			companyId, name, scope, primKey, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_N_S_P_PrevAndNext(
			long resourcePermissionId, long companyId, String name, int scope,
			String primKey,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		name = Objects.toString(name, "");
		primKey = Objects.toString(primKey, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_N_S_P_PrevAndNext(
				session, resourcePermission, companyId, name, scope, primKey,
				orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_N_S_P_PrevAndNext(
				session, resourcePermission, companyId, name, scope, primKey,
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

	protected ResourcePermission getByC_N_S_P_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		String name, int scope, String primKey,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_P_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_P_SCOPE_2);

		boolean bindPrimKey = false;

		if (primKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_3);
		}
		else {
			bindPrimKey = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_2);
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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(scope);

		if (bindPrimKey) {
			queryPos.add(primKey);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKeys the prim keys
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String[] primKeys) {

		return findByC_N_S_P(
			companyId, name, scope, primKeys, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKeys the prim keys
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String[] primKeys, int start,
		int end) {

		return findByC_N_S_P(
			companyId, name, scope, primKeys, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKeys the prim keys
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String[] primKeys, int start,
		int end, OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_P(
			companyId, name, scope, primKeys, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKeys the prim keys
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P(
		long companyId, String name, int scope, String[] primKeys, int start,
		int end, OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		if (primKeys == null) {
			primKeys = new String[0];
		}
		else if (primKeys.length > 1) {
			for (int i = 0; i < primKeys.length; i++) {
				primKeys[i] = Objects.toString(primKeys[i], "");
			}

			primKeys = ArrayUtil.sortedUnique(primKeys);
		}

		if (primKeys.length == 1) {
			return findByC_N_S_P(
				companyId, name, scope, primKeys[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						companyId, name, scope, StringUtil.merge(primKeys)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, name, scope, StringUtil.merge(primKeys), start,
					end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByC_N_S_P, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							!ArrayUtil.contains(
								primKeys, resourcePermission.getPrimKey())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_SCOPE_2);

				if (primKeys.length > 0) {
					sb.append("(");

					for (int i = 0; i < primKeys.length; i++) {
						String primKey = primKeys[i];

						if (primKey.isEmpty()) {
							sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_3);
						}
						else {
							sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_2);
						}

						if ((i + 1) < primKeys.length) {
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
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(scope);

					for (String primKey : primKeys) {
						if ((primKey != null) && !primKey.isEmpty()) {
							queryPos.add(primKey);
						}
					}

					list = (List<ResourcePermission>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByC_N_S_P, finderArgs,
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
	 * Removes all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 */
	@Override
	public void removeByC_N_S_P(
		long companyId, String name, int scope, String primKey) {

		for (ResourcePermission resourcePermission :
				findByC_N_S_P(
					companyId, name, scope, primKey, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_P(
		long companyId, String name, int scope, String primKey) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");
			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = _finderPathCountByC_N_S_P;

			Object[] finderArgs = new Object[] {
				companyId, name, scope, primKey
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_2);
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

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
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
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKeys the prim keys
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_P(
		long companyId, String name, int scope, String[] primKeys) {

		name = Objects.toString(name, "");

		if (primKeys == null) {
			primKeys = new String[0];
		}
		else if (primKeys.length > 1) {
			for (int i = 0; i < primKeys.length; i++) {
				primKeys[i] = Objects.toString(primKeys[i], "");
			}

			primKeys = ArrayUtil.sortedUnique(primKeys);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = new Object[] {
				companyId, name, scope, StringUtil.merge(primKeys)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByC_N_S_P, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_SCOPE_2);

				if (primKeys.length > 0) {
					sb.append("(");

					for (int i = 0; i < primKeys.length; i++) {
						String primKey = primKeys[i];

						if (primKey.isEmpty()) {
							sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_3);
						}
						else {
							sb.append(_FINDER_COLUMN_C_N_S_P_PRIMKEY_2);
						}

						if ((i + 1) < primKeys.length) {
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

					if (bindName) {
						queryPos.add(name);
					}

					queryPos.add(scope);

					for (String primKey : primKeys) {
						if ((primKey != null) && !primKey.isEmpty()) {
							queryPos.add(primKey);
						}
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByC_N_S_P, finderArgs,
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

	private static final String _FINDER_COLUMN_C_N_S_P_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_NAME_2 =
		"resourcePermission.name = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_SCOPE_2 =
		"resourcePermission.scope = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_PRIMKEY_2 =
		"resourcePermission.primKey = ?";

	private static final String _FINDER_COLUMN_C_N_S_P_PRIMKEY_3 =
		"(resourcePermission.primKey IS NULL OR resourcePermission.primKey = '')";

	private FinderPath _finderPathWithPaginationFindByC_N_S_R;
	private FinderPath _finderPathWithoutPaginationFindByC_N_S_R;
	private FinderPath _finderPathCountByC_N_S_R;

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R(
		long companyId, String name, int scope, long roleId) {

		return findByC_N_S_R(
			companyId, name, scope, roleId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R(
		long companyId, String name, int scope, long roleId, int start,
		int end) {

		return findByC_N_S_R(companyId, name, scope, roleId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R(
		long companyId, String name, int scope, long roleId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_R(
			companyId, name, scope, roleId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R(
		long companyId, String name, int scope, long roleId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_N_S_R;
					finderArgs = new Object[] {companyId, name, scope, roleId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_N_S_R;
				finderArgs = new Object[] {
					companyId, name, scope, roleId, start, end,
					orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							(roleId != resourcePermission.getRoleId())) {

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

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_R_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_R_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_R_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_R_SCOPE_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_ROLEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(scope);

					queryPos.add(roleId);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_R_First(
			long companyId, String name, int scope, long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_R_First(
			companyId, name, scope, roleId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", roleId=");
		sb.append(roleId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_R_First(
		long companyId, String name, int scope, long roleId,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_N_S_R(
			companyId, name, scope, roleId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_R_Last(
			long companyId, String name, int scope, long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_R_Last(
			companyId, name, scope, roleId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", roleId=");
		sb.append(roleId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_R_Last(
		long companyId, String name, int scope, long roleId,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_N_S_R(companyId, name, scope, roleId);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_N_S_R(
			companyId, name, scope, roleId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_N_S_R_PrevAndNext(
			long resourcePermissionId, long companyId, String name, int scope,
			long roleId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		name = Objects.toString(name, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_N_S_R_PrevAndNext(
				session, resourcePermission, companyId, name, scope, roleId,
				orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_N_S_R_PrevAndNext(
				session, resourcePermission, companyId, name, scope, roleId,
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

	protected ResourcePermission getByC_N_S_R_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		String name, int scope, long roleId,
		OrderByComparator<ResourcePermission> orderByComparator,
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

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_R_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_R_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_R_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_SCOPE_2);

		sb.append(_FINDER_COLUMN_C_N_S_R_ROLEID_2);

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(scope);

		queryPos.add(roleId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 */
	@Override
	public void removeByC_N_S_R(
		long companyId, String name, int scope, long roleId) {

		for (ResourcePermission resourcePermission :
				findByC_N_S_R(
					companyId, name, scope, roleId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_R(
		long companyId, String name, int scope, long roleId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByC_N_S_R;

			Object[] finderArgs = new Object[] {companyId, name, scope, roleId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_R_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_R_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_R_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_R_SCOPE_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_ROLEID_2);

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

					queryPos.add(scope);

					queryPos.add(roleId);

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

	private static final String _FINDER_COLUMN_C_N_S_R_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_NAME_2 =
		"resourcePermission.name = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_SCOPE_2 =
		"resourcePermission.scope = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_ROLEID_2 =
		"resourcePermission.roleId = ?";

	private FinderPath _finderPathWithPaginationFindByC_N_S_P_R;
	private FinderPath _finderPathWithoutPaginationFindByC_N_S_P_R;
	private FinderPath _finderPathFetchByC_N_S_P_R;
	private FinderPath _finderPathCountByC_N_S_P_R;
	private FinderPath _finderPathWithPaginationCountByC_N_S_P_R;

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleIds the role IDs
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P_R(
		long companyId, String name, int scope, String primKey,
		long[] roleIds) {

		return findByC_N_S_P_R(
			companyId, name, scope, primKey, roleIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleIds the role IDs
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long[] roleIds,
		int start, int end) {

		return findByC_N_S_P_R(
			companyId, name, scope, primKey, roleIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleIds the role IDs
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long[] roleIds,
		int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_P_R(
			companyId, name, scope, primKey, roleIds, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleIds the role IDs
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long[] roleIds,
		int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");
		primKey = Objects.toString(primKey, "");

		if (roleIds == null) {
			roleIds = new long[0];
		}
		else if (roleIds.length > 1) {
			roleIds = ArrayUtil.sortedUnique(roleIds);
		}

		if (roleIds.length == 1) {
			ResourcePermission resourcePermission = fetchByC_N_S_P_R(
				companyId, name, scope, primKey, roleIds[0]);

			if (resourcePermission == null) {
				return Collections.emptyList();
			}
			else {
				return Collections.singletonList(resourcePermission);
			}
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						companyId, name, scope, primKey,
						StringUtil.merge(roleIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, name, scope, primKey, StringUtil.merge(roleIds),
					start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByC_N_S_P_R, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							!primKey.equals(resourcePermission.getPrimKey()) ||
							!ArrayUtil.contains(
								roleIds, resourcePermission.getRoleId())) {

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
						(roleIds.length > databaseInMaxParameters)) {

						list = new ArrayList<ResourcePermission>();

						long[][] roleIdsPages = (long[][])ArrayUtil.split(
							roleIds, databaseInMaxParameters);

						for (long[] roleIdsPage : roleIdsPages) {
							list.addAll(
								_findByC_N_S_P_R(
									companyId, name, scope, primKey,
									roleIdsPage, start, end,
									orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByC_N_S_P_R(
							companyId, name, scope, primKey, roleIds, start,
							end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByC_N_S_P_R,
							finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<ResourcePermission> _findByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long[] roleIds,
		int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_P_R_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_P_R_SCOPE_2);

		boolean bindPrimKey = false;

		if (primKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_3);
		}
		else {
			bindPrimKey = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_2);
		}

		if (roleIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_N_S_P_R_ROLEID_7);

			sb.append(StringUtil.merge(roleIds));

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

			queryPos.add(scope);

			if (bindPrimKey) {
				queryPos.add(primKey);
			}

			list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the resource permission where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63; or throws a <code>NoSuchResourcePermissionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleId the role ID
	 * @return the matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_P_R(
			long companyId, String name, int scope, String primKey, long roleId)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_P_R(
			companyId, name, scope, primKey, roleId);

		if (resourcePermission == null) {
			StringBundler sb = new StringBundler(12);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", scope=");
			sb.append(scope);

			sb.append(", primKey=");
			sb.append(primKey);

			sb.append(", roleId=");
			sb.append(roleId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchResourcePermissionException(sb.toString());
		}

		return resourcePermission;
	}

	/**
	 * Returns the resource permission where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleId the role ID
	 * @return the matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long roleId) {

		return fetchByC_N_S_P_R(companyId, name, scope, primKey, roleId, true);
	}

	/**
	 * Returns the resource permission where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long roleId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");
			primKey = Objects.toString(primKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, name, scope, primKey, roleId
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_N_S_P_R, finderArgs, this);
			}

			if (result instanceof ResourcePermission) {
				ResourcePermission resourcePermission =
					(ResourcePermission)result;

				if ((companyId != resourcePermission.getCompanyId()) ||
					!Objects.equals(name, resourcePermission.getName()) ||
					(scope != resourcePermission.getScope()) ||
					!Objects.equals(primKey, resourcePermission.getPrimKey()) ||
					(roleId != resourcePermission.getRoleId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(7);

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_R_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_R_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_R_ROLEID_2);

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

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
					}

					queryPos.add(roleId);

					List<ResourcePermission> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_N_S_P_R, finderArgs, list);
						}
					}
					else {
						ResourcePermission resourcePermission = list.get(0);

						result = resourcePermission;

						cacheResult(resourcePermission);
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
				return (ResourcePermission)result;
			}
		}
	}

	/**
	 * Removes the resource permission where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleId the role ID
	 * @return the resource permission that was removed
	 */
	@Override
	public ResourcePermission removeByC_N_S_P_R(
			long companyId, String name, int scope, String primKey, long roleId)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = findByC_N_S_P_R(
			companyId, name, scope, primKey, roleId);

		return remove(resourcePermission);
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleId the role ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_P_R(
		long companyId, String name, int scope, String primKey, long roleId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");
			primKey = Objects.toString(primKey, "");

			FinderPath finderPath = _finderPathCountByC_N_S_P_R;

			Object[] finderArgs = new Object[] {
				companyId, name, scope, primKey, roleId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_P_R_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_R_SCOPE_2);

				boolean bindPrimKey = false;

				if (primKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_3);
				}
				else {
					bindPrimKey = true;

					sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_P_R_ROLEID_2);

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

					queryPos.add(scope);

					if (bindPrimKey) {
						queryPos.add(primKey);
					}

					queryPos.add(roleId);

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
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and primKey = &#63; and roleId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param primKey the prim key
	 * @param roleIds the role IDs
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_P_R(
		long companyId, String name, int scope, String primKey,
		long[] roleIds) {

		name = Objects.toString(name, "");
		primKey = Objects.toString(primKey, "");

		if (roleIds == null) {
			roleIds = new long[0];
		}
		else if (roleIds.length > 1) {
			roleIds = ArrayUtil.sortedUnique(roleIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = new Object[] {
				companyId, name, scope, primKey, StringUtil.merge(roleIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByC_N_S_P_R, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(roleIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] roleIdsPages = (long[][])ArrayUtil.split(
							roleIds, databaseInMaxParameters);

						for (long[] roleIdsPage : roleIdsPages) {
							count += Long.valueOf(
								_countByC_N_S_P_R(
									companyId, name, scope, primKey,
									roleIdsPage));
						}
					}
					else {
						count = Long.valueOf(
							_countByC_N_S_P_R(
								companyId, name, scope, primKey, roleIds));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByC_N_S_P_R, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByC_N_S_P_R(
		long companyId, String name, int scope, String primKey,
		long[] roleIds) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_P_R_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_R_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_P_R_SCOPE_2);

		boolean bindPrimKey = false;

		if (primKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_3);
		}
		else {
			bindPrimKey = true;

			sb.append(_FINDER_COLUMN_C_N_S_P_R_PRIMKEY_2);
		}

		if (roleIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_N_S_P_R_ROLEID_7);

			sb.append(StringUtil.merge(roleIds));

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

			queryPos.add(companyId);

			if (bindName) {
				queryPos.add(name);
			}

			queryPos.add(scope);

			if (bindPrimKey) {
				queryPos.add(primKey);
			}

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

	private static final String _FINDER_COLUMN_C_N_S_P_R_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_NAME_2 =
		"resourcePermission.name = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_SCOPE_2 =
		"resourcePermission.scope = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_PRIMKEY_2 =
		"resourcePermission.primKey = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_PRIMKEY_3 =
		"(resourcePermission.primKey IS NULL OR resourcePermission.primKey = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_P_R_ROLEID_2 =
		"resourcePermission.roleId = ?";

	private static final String _FINDER_COLUMN_C_N_S_P_R_ROLEID_7 =
		"resourcePermission.roleId IN (";

	private FinderPath _finderPathWithPaginationFindByC_N_S_R_V;
	private FinderPath _finderPathWithoutPaginationFindByC_N_S_R_V;
	private FinderPath _finderPathCountByC_N_S_R_V;
	private FinderPath _finderPathWithPaginationCountByC_N_S_R_V;

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId, int start, int end) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_N_S_R_V;
					finderArgs = new Object[] {
						companyId, name, scope, roleId, viewActionId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_N_S_R_V;
				finderArgs = new Object[] {
					companyId, name, scope, roleId, viewActionId, start, end,
					orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							(roleId != resourcePermission.getRoleId()) ||
							(viewActionId !=
								resourcePermission.isViewActionId())) {

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
						7 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(7);
				}

				sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_R_V_SCOPE_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_ROLEID_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

					queryPos.add(scope);

					queryPos.add(roleId);

					queryPos.add(viewActionId);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_R_V_First(
			long companyId, String name, int scope, long roleId,
			boolean viewActionId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_R_V_First(
			companyId, name, scope, roleId, viewActionId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", roleId=");
		sb.append(roleId);

		sb.append(", viewActionId=");
		sb.append(viewActionId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the first resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_R_V_First(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = findByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission
	 * @throws NoSuchResourcePermissionException if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission findByC_N_S_R_V_Last(
			long companyId, String name, int scope, long roleId,
			boolean viewActionId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByC_N_S_R_V_Last(
			companyId, name, scope, roleId, viewActionId, orderByComparator);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		StringBundler sb = new StringBundler(12);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append(", scope=");
		sb.append(scope);

		sb.append(", roleId=");
		sb.append(roleId);

		sb.append(", viewActionId=");
		sb.append(viewActionId);

		sb.append("}");

		throw new NoSuchResourcePermissionException(sb.toString());
	}

	/**
	 * Returns the last resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching resource permission, or <code>null</code> if a matching resource permission could not be found
	 */
	@Override
	public ResourcePermission fetchByC_N_S_R_V_Last(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId,
		OrderByComparator<ResourcePermission> orderByComparator) {

		int count = countByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId);

		if (count == 0) {
			return null;
		}

		List<ResourcePermission> list = findByC_N_S_R_V(
			companyId, name, scope, roleId, viewActionId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the resource permissions before and after the current resource permission in the ordered set where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param resourcePermissionId the primary key of the current resource permission
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission[] findByC_N_S_R_V_PrevAndNext(
			long resourcePermissionId, long companyId, String name, int scope,
			long roleId, boolean viewActionId,
			OrderByComparator<ResourcePermission> orderByComparator)
		throws NoSuchResourcePermissionException {

		name = Objects.toString(name, "");

		ResourcePermission resourcePermission = findByPrimaryKey(
			resourcePermissionId);

		Session session = null;

		try {
			session = openSession();

			ResourcePermission[] array = new ResourcePermissionImpl[3];

			array[0] = getByC_N_S_R_V_PrevAndNext(
				session, resourcePermission, companyId, name, scope, roleId,
				viewActionId, orderByComparator, true);

			array[1] = resourcePermission;

			array[2] = getByC_N_S_R_V_PrevAndNext(
				session, resourcePermission, companyId, name, scope, roleId,
				viewActionId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected ResourcePermission getByC_N_S_R_V_PrevAndNext(
		Session session, ResourcePermission resourcePermission, long companyId,
		String name, int scope, long roleId, boolean viewActionId,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_R_V_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_V_SCOPE_2);

		sb.append(_FINDER_COLUMN_C_N_S_R_V_ROLEID_2);

		sb.append(_FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2);

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
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		queryPos.add(scope);

		queryPos.add(roleId);

		queryPos.add(viewActionId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						resourcePermission)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<ResourcePermission> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = any &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleIds the role IDs
	 * @param viewActionId the view action ID
	 * @return the matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleIds, viewActionId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = any &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleIds the role IDs
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId, int start, int end) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleIds, viewActionId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = any &#63; and viewActionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleIds the role IDs
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findByC_N_S_R_V(
			companyId, name, scope, roleIds, viewActionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleIds the role IDs
	 * @param viewActionId the view action ID
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching resource permissions
	 */
	@Override
	public List<ResourcePermission> findByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		if (roleIds == null) {
			roleIds = new long[0];
		}
		else if (roleIds.length > 1) {
			roleIds = ArrayUtil.sortedUnique(roleIds);
		}

		if (roleIds.length == 1) {
			return findByC_N_S_R_V(
				companyId, name, scope, roleIds[0], viewActionId, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						companyId, name, scope, StringUtil.merge(roleIds),
						viewActionId
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					companyId, name, scope, StringUtil.merge(roleIds),
					viewActionId, start, end, orderByComparator
				};
			}

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByC_N_S_R_V, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (ResourcePermission resourcePermission : list) {
						if ((companyId != resourcePermission.getCompanyId()) ||
							!name.equals(resourcePermission.getName()) ||
							(scope != resourcePermission.getScope()) ||
							!ArrayUtil.contains(
								roleIds, resourcePermission.getRoleId()) ||
							(viewActionId !=
								resourcePermission.isViewActionId())) {

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
						(roleIds.length > databaseInMaxParameters)) {

						list = new ArrayList<ResourcePermission>();

						long[][] roleIdsPages = (long[][])ArrayUtil.split(
							roleIds, databaseInMaxParameters);

						for (long[] roleIdsPage : roleIdsPages) {
							list.addAll(
								_findByC_N_S_R_V(
									companyId, name, scope, roleIdsPage,
									viewActionId, start, end,
									orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByC_N_S_R_V(
							companyId, name, scope, roleIds, viewActionId,
							start, end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByC_N_S_R_V,
							finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<ResourcePermission> _findByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId, int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		List<ResourcePermission> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_R_V_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_V_SCOPE_2);

		if (roleIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_N_S_R_V_ROLEID_7);

			sb.append(StringUtil.merge(roleIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
		}
		else {
			sb.append(ResourcePermissionModelImpl.ORDER_BY_JPQL);
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

			queryPos.add(scope);

			queryPos.add(viewActionId);

			list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Removes all the resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 */
	@Override
	public void removeByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId) {

		for (ResourcePermission resourcePermission :
				findByC_N_S_R_V(
					companyId, name, scope, roleId, viewActionId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleId the role ID
	 * @param viewActionId the view action ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_R_V(
		long companyId, String name, int scope, long roleId,
		boolean viewActionId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathCountByC_N_S_R_V;

			Object[] finderArgs = new Object[] {
				companyId, name, scope, roleId, viewActionId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_2);
				}

				sb.append(_FINDER_COLUMN_C_N_S_R_V_SCOPE_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_ROLEID_2);

				sb.append(_FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2);

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

					queryPos.add(scope);

					queryPos.add(roleId);

					queryPos.add(viewActionId);

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
	 * Returns the number of resource permissions where companyId = &#63; and name = &#63; and scope = &#63; and roleId = any &#63; and viewActionId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param scope the scope
	 * @param roleIds the role IDs
	 * @param viewActionId the view action ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId) {

		name = Objects.toString(name, "");

		if (roleIds == null) {
			roleIds = new long[0];
		}
		else if (roleIds.length > 1) {
			roleIds = ArrayUtil.sortedUnique(roleIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Object[] finderArgs = new Object[] {
				companyId, name, scope, StringUtil.merge(roleIds), viewActionId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByC_N_S_R_V, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(roleIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] roleIdsPages = (long[][])ArrayUtil.split(
							roleIds, databaseInMaxParameters);

						for (long[] roleIdsPage : roleIdsPages) {
							count += Long.valueOf(
								_countByC_N_S_R_V(
									companyId, name, scope, roleIdsPage,
									viewActionId));
						}
					}
					else {
						count = Long.valueOf(
							_countByC_N_S_R_V(
								companyId, name, scope, roleIds, viewActionId));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByC_N_S_R_V, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByC_N_S_R_V(
		long companyId, String name, int scope, long[] roleIds,
		boolean viewActionId) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_RESOURCEPERMISSION_WHERE);

		sb.append(_FINDER_COLUMN_C_N_S_R_V_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_S_R_V_NAME_2);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_V_SCOPE_2);

		if (roleIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_C_N_S_R_V_ROLEID_7);

			sb.append(StringUtil.merge(roleIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

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

			queryPos.add(scope);

			queryPos.add(viewActionId);

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

	private static final String _FINDER_COLUMN_C_N_S_R_V_COMPANYID_2 =
		"resourcePermission.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_V_NAME_2 =
		"resourcePermission.name = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_V_NAME_3 =
		"(resourcePermission.name IS NULL OR resourcePermission.name = '') AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_V_SCOPE_2 =
		"resourcePermission.scope = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_V_ROLEID_2 =
		"resourcePermission.roleId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_S_R_V_ROLEID_7 =
		"resourcePermission.roleId IN (";

	private static final String _FINDER_COLUMN_C_N_S_R_V_VIEWACTIONID_2 =
		"resourcePermission.viewActionId = ?";

	public ResourcePermissionPersistenceImpl() {
		setModelClass(ResourcePermission.class);

		setModelImplClass(ResourcePermissionImpl.class);
		setModelPKClass(long.class);

		setTable(ResourcePermissionTable.INSTANCE);
	}

	/**
	 * Caches the resource permission in the entity cache if it is enabled.
	 *
	 * @param resourcePermission the resource permission
	 */
	@Override
	public void cacheResult(ResourcePermission resourcePermission) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					resourcePermission.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				ResourcePermissionImpl.class,
				resourcePermission.getPrimaryKey(), resourcePermission);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_N_S_P_R,
				new Object[] {
					resourcePermission.getCompanyId(),
					resourcePermission.getName(), resourcePermission.getScope(),
					resourcePermission.getPrimKey(),
					resourcePermission.getRoleId()
				},
				resourcePermission);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the resource permissions in the entity cache if it is enabled.
	 *
	 * @param resourcePermissions the resource permissions
	 */
	@Override
	public void cacheResult(List<ResourcePermission> resourcePermissions) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (resourcePermissions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ResourcePermission resourcePermission : resourcePermissions) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						resourcePermission.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						ResourcePermissionImpl.class,
						resourcePermission.getPrimaryKey()) == null) {

					cacheResult(resourcePermission);
				}
			}
		}
	}

	/**
	 * Clears the cache for all resource permissions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(ResourcePermissionImpl.class);

		FinderCacheUtil.clearCache(ResourcePermissionImpl.class);
	}

	/**
	 * Clears the cache for the resource permission.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ResourcePermission resourcePermission) {
		EntityCacheUtil.removeResult(
			ResourcePermissionImpl.class, resourcePermission);
	}

	@Override
	public void clearCache(List<ResourcePermission> resourcePermissions) {
		for (ResourcePermission resourcePermission : resourcePermissions) {
			EntityCacheUtil.removeResult(
				ResourcePermissionImpl.class, resourcePermission);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(ResourcePermissionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(
				ResourcePermissionImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ResourcePermissionModelImpl resourcePermissionModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					resourcePermissionModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				resourcePermissionModelImpl.getCompanyId(),
				resourcePermissionModelImpl.getName(),
				resourcePermissionModelImpl.getScope(),
				resourcePermissionModelImpl.getPrimKey(),
				resourcePermissionModelImpl.getRoleId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_N_S_P_R, args, resourcePermissionModelImpl);
		}
	}

	/**
	 * Creates a new resource permission with the primary key. Does not add the resource permission to the database.
	 *
	 * @param resourcePermissionId the primary key for the new resource permission
	 * @return the new resource permission
	 */
	@Override
	public ResourcePermission create(long resourcePermissionId) {
		ResourcePermission resourcePermission = new ResourcePermissionImpl();

		resourcePermission.setNew(true);
		resourcePermission.setPrimaryKey(resourcePermissionId);

		resourcePermission.setCompanyId(CompanyThreadLocal.getCompanyId());

		return resourcePermission;
	}

	/**
	 * Removes the resource permission with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @return the resource permission that was removed
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission remove(long resourcePermissionId)
		throws NoSuchResourcePermissionException {

		return remove((Serializable)resourcePermissionId);
	}

	/**
	 * Removes the resource permission with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the resource permission
	 * @return the resource permission that was removed
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission remove(Serializable primaryKey)
		throws NoSuchResourcePermissionException {

		Session session = null;

		try {
			session = openSession();

			ResourcePermission resourcePermission =
				(ResourcePermission)session.get(
					ResourcePermissionImpl.class, primaryKey);

			if (resourcePermission == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchResourcePermissionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(resourcePermission);
		}
		catch (NoSuchResourcePermissionException noSuchEntityException) {
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
	protected ResourcePermission removeImpl(
		ResourcePermission resourcePermission) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(resourcePermission)) {
				resourcePermission = (ResourcePermission)session.get(
					ResourcePermissionImpl.class,
					resourcePermission.getPrimaryKeyObj());
			}

			if ((resourcePermission != null) &&
				CTPersistenceHelperUtil.isRemove(resourcePermission)) {

				session.delete(resourcePermission);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (resourcePermission != null) {
			clearCache(resourcePermission);
		}

		return resourcePermission;
	}

	@Override
	public ResourcePermission updateImpl(
		ResourcePermission resourcePermission) {

		boolean isNew = resourcePermission.isNew();

		if (!(resourcePermission instanceof ResourcePermissionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(resourcePermission.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					resourcePermission);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in resourcePermission proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ResourcePermission implementation " +
					resourcePermission.getClass());
		}

		ResourcePermissionModelImpl resourcePermissionModelImpl =
			(ResourcePermissionModelImpl)resourcePermission;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(resourcePermission)) {
				if (!isNew) {
					session.evict(
						ResourcePermissionImpl.class,
						resourcePermission.getPrimaryKeyObj());
				}

				session.save(resourcePermission);
			}
			else {
				resourcePermission = (ResourcePermission)session.merge(
					resourcePermission);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			ResourcePermissionImpl.class, resourcePermissionModelImpl, false,
			true);

		cacheUniqueFindersCache(resourcePermissionModelImpl);

		if (isNew) {
			resourcePermission.setNew(false);
		}

		resourcePermission.resetOriginalValues();

		return resourcePermission;
	}

	/**
	 * Returns the resource permission with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the resource permission
	 * @return the resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission findByPrimaryKey(Serializable primaryKey)
		throws NoSuchResourcePermissionException {

		ResourcePermission resourcePermission = fetchByPrimaryKey(primaryKey);

		if (resourcePermission == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchResourcePermissionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return resourcePermission;
	}

	/**
	 * Returns the resource permission with the primary key or throws a <code>NoSuchResourcePermissionException</code> if it could not be found.
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @return the resource permission
	 * @throws NoSuchResourcePermissionException if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission findByPrimaryKey(long resourcePermissionId)
		throws NoSuchResourcePermissionException {

		return findByPrimaryKey((Serializable)resourcePermissionId);
	}

	/**
	 * Returns the resource permission with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the resource permission
	 * @return the resource permission, or <code>null</code> if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				ResourcePermission.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		ResourcePermission resourcePermission =
			(ResourcePermission)EntityCacheUtil.getResult(
				ResourcePermissionImpl.class, primaryKey);

		if (resourcePermission != null) {
			return resourcePermission;
		}

		Session session = null;

		try {
			session = openSession();

			resourcePermission = (ResourcePermission)session.get(
				ResourcePermissionImpl.class, primaryKey);

			if (resourcePermission != null) {
				cacheResult(resourcePermission);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return resourcePermission;
	}

	/**
	 * Returns the resource permission with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param resourcePermissionId the primary key of the resource permission
	 * @return the resource permission, or <code>null</code> if a resource permission with the primary key could not be found
	 */
	@Override
	public ResourcePermission fetchByPrimaryKey(long resourcePermissionId) {
		return fetchByPrimaryKey((Serializable)resourcePermissionId);
	}

	@Override
	public Map<Serializable, ResourcePermission> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(
				ResourcePermission.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, ResourcePermission> map =
			new HashMap<Serializable, ResourcePermission>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			ResourcePermission resourcePermission = fetchByPrimaryKey(
				primaryKey);

			if (resourcePermission != null) {
				map.put(primaryKey, resourcePermission);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						ResourcePermission.class, primaryKey)) {

				ResourcePermission resourcePermission =
					(ResourcePermission)EntityCacheUtil.getResult(
						ResourcePermissionImpl.class, primaryKey);

				if (resourcePermission == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, resourcePermission);
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

			for (ResourcePermission resourcePermission :
					(List<ResourcePermission>)query.list()) {

				map.put(
					resourcePermission.getPrimaryKeyObj(), resourcePermission);

				cacheResult(resourcePermission);
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
	 * Returns all the resource permissions.
	 *
	 * @return the resource permissions
	 */
	@Override
	public List<ResourcePermission> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the resource permissions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @return the range of resource permissions
	 */
	@Override
	public List<ResourcePermission> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the resource permissions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of resource permissions
	 */
	@Override
	public List<ResourcePermission> findAll(
		int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the resource permissions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ResourcePermissionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of resource permissions
	 * @param end the upper bound of the range of resource permissions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of resource permissions
	 */
	@Override
	public List<ResourcePermission> findAll(
		int start, int end,
		OrderByComparator<ResourcePermission> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

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

			List<ResourcePermission> list = null;

			if (useFinderCache) {
				list = (List<ResourcePermission>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_RESOURCEPERMISSION);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_RESOURCEPERMISSION;

					sql = sql.concat(ResourcePermissionModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<ResourcePermission>)QueryUtil.list(
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
	 * Removes all the resource permissions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ResourcePermission resourcePermission : findAll()) {
			remove(resourcePermission);
		}
	}

	/**
	 * Returns the number of resource permissions.
	 *
	 * @return the number of resource permissions
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					ResourcePermission.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_RESOURCEPERMISSION);

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
		return "resourcePermissionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RESOURCEPERMISSION;
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
		return ResourcePermissionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ResourcePermission";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("scope");
		ctMergeColumnNames.add("primKey");
		ctMergeColumnNames.add("primKeyId");
		ctMergeColumnNames.add("roleId");
		ctMergeColumnNames.add("ownerId");
		ctMergeColumnNames.add("actionIds");
		ctMergeColumnNames.add("viewActionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("resourcePermissionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "name", "scope", "primKey", "roleId"});
	}

	/**
	 * Initializes the resource permission persistence.
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

		_finderPathWithPaginationFindByName = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByName",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"name"}, true);

		_finderPathWithoutPaginationFindByName = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByName",
			new String[] {String.class.getName()}, new String[] {"name"}, true);

		_finderPathCountByName = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName",
			new String[] {String.class.getName()}, new String[] {"name"},
			false);

		_finderPathWithPaginationFindByScope = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByScope",
			new String[] {
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"scope"}, true);

		_finderPathWithoutPaginationFindByScope = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByScope",
			new String[] {Integer.class.getName()}, new String[] {"scope"},
			true);

		_finderPathCountByScope = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByScope",
			new String[] {Integer.class.getName()}, new String[] {"scope"},
			false);

		_finderPathWithPaginationCountByScope = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByScope",
			new String[] {Integer.class.getName()}, new String[] {"scope"},
			false);

		_finderPathWithPaginationFindByRoleId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRoleId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"roleId"}, true);

		_finderPathWithoutPaginationFindByRoleId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRoleId",
			new String[] {Long.class.getName()}, new String[] {"roleId"}, true);

		_finderPathCountByRoleId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRoleId",
			new String[] {Long.class.getName()}, new String[] {"roleId"},
			false);

		_finderPathWithPaginationFindByC_LikeP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeP",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "primKey"}, true);

		_finderPathWithPaginationCountByC_LikeP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeP",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "primKey"}, false);

		_finderPathWithPaginationFindByC_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name", "scope"}, true);

		_finderPathWithoutPaginationFindByC_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "name", "scope"}, true);

		_finderPathCountByC_N_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {"companyId", "name", "scope"}, false);

		_finderPathWithPaginationFindByC_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_P",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "scope", "primKey"}, true);

		_finderPathWithoutPaginationFindByC_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_P",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "scope", "primKey"}, true);

		_finderPathCountByC_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_P",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				String.class.getName()
			},
			new String[] {"companyId", "scope", "primKey"}, false);

		_finderPathWithPaginationFindByC_N_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey"}, true);

		_finderPathWithoutPaginationFindByC_N_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey"}, true);

		_finderPathCountByC_N_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey"}, false);

		_finderPathWithPaginationCountByC_N_S_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey"}, false);

		_finderPathWithPaginationFindByC_N_S_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name", "scope", "roleId"}, true);

		_finderPathWithoutPaginationFindByC_N_S_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "roleId"}, true);

		_finderPathCountByC_N_S_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "roleId"}, false);

		_finderPathWithPaginationFindByC_N_S_P_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_P_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey", "roleId"},
			true);

		_finderPathWithoutPaginationFindByC_N_S_P_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_P_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey", "roleId"},
			true);

		_finderPathFetchByC_N_S_P_R = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N_S_P_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey", "roleId"},
			true);

		_finderPathCountByC_N_S_P_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S_P_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey", "roleId"},
			false);

		_finderPathWithPaginationCountByC_N_S_P_R = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_P_R",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"companyId", "name", "scope", "primKey", "roleId"},
			false);

		_finderPathWithPaginationFindByC_N_S_R_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_R_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "name", "scope", "roleId", "viewActionId"
			},
			true);

		_finderPathWithoutPaginationFindByC_N_S_R_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_R_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {
				"companyId", "name", "scope", "roleId", "viewActionId"
			},
			true);

		_finderPathCountByC_N_S_R_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S_R_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {
				"companyId", "name", "scope", "roleId", "viewActionId"
			},
			false);

		_finderPathWithPaginationCountByC_N_S_R_V = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_R_V",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {
				"companyId", "name", "scope", "roleId", "viewActionId"
			},
			false);

		ResourcePermissionUtil.setPersistence(this);
	}

	public void destroy() {
		ResourcePermissionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ResourcePermissionImpl.class.getName());
	}

	private static final String _SQL_SELECT_RESOURCEPERMISSION =
		"SELECT resourcePermission FROM ResourcePermission resourcePermission";

	private static final String _SQL_SELECT_RESOURCEPERMISSION_WHERE =
		"SELECT resourcePermission FROM ResourcePermission resourcePermission WHERE ";

	private static final String _SQL_COUNT_RESOURCEPERMISSION =
		"SELECT COUNT(resourcePermission) FROM ResourcePermission resourcePermission";

	private static final String _SQL_COUNT_RESOURCEPERMISSION_WHERE =
		"SELECT COUNT(resourcePermission) FROM ResourcePermission resourcePermission WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "resourcePermission.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ResourcePermission exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ResourcePermission exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ResourcePermissionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}