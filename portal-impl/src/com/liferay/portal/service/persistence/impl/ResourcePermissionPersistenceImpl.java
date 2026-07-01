/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.ResourcePermissionImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	extends BasePersistenceImpl
		<ResourcePermission, NoSuchResourcePermissionException>
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

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByName;

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

		return _collectionPersistenceFinderByName.find(
			FinderCacheUtil.getFinderCache(), new Object[] {name}, start, end,
			orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByName.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name},
			orderByComparator);
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

		return _collectionPersistenceFinderByName.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name},
			orderByComparator);
	}

	/**
	 * Removes all the resource permissions where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName(String name) {
		_collectionPersistenceFinderByName.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {name});
	}

	/**
	 * Returns the number of resource permissions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByName(String name) {
		return _collectionPersistenceFinderByName.count(
			FinderCacheUtil.getFinderCache(), new Object[] {name});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByScope;

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

		return _collectionPersistenceFinderByScope.find(
			FinderCacheUtil.getFinderCache(), new Object[] {new int[] {scope}},
			start, end, orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByScope.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {new int[] {scope}},
			orderByComparator);
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

		return _collectionPersistenceFinderByScope.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {new int[] {scope}},
			orderByComparator);
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

		return _collectionPersistenceFinderByScope.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(scopes)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the resource permissions where scope = &#63; from the database.
	 *
	 * @param scope the scope
	 */
	@Override
	public void removeByScope(int scope) {
		_collectionPersistenceFinderByScope.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {new int[] {scope}});
	}

	/**
	 * Returns the number of resource permissions where scope = &#63;.
	 *
	 * @param scope the scope
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByScope(int scope) {
		return _collectionPersistenceFinderByScope.count(
			FinderCacheUtil.getFinderCache(), new Object[] {new int[] {scope}});
	}

	/**
	 * Returns the number of resource permissions where scope = any &#63;.
	 *
	 * @param scopes the scopes
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByScope(int[] scopes) {
		return _collectionPersistenceFinderByScope.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(scopes)});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByRoleId;

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

		return _collectionPersistenceFinderByRoleId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId}, start, end,
			orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByRoleId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
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

		return _collectionPersistenceFinderByRoleId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
	}

	/**
	 * Removes all the resource permissions where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 */
	@Override
	public void removeByRoleId(long roleId) {
		_collectionPersistenceFinderByRoleId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	/**
	 * Returns the number of resource permissions where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching resource permissions
	 */
	@Override
	public int countByRoleId(long roleId) {
		return _collectionPersistenceFinderByRoleId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_LikeP;

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

		return _collectionPersistenceFinderByC_LikeP.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, primKey},
			start, end, orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_LikeP.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, primKey},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_LikeP.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, primKey},
			orderByComparator);
	}

	/**
	 * Removes all the resource permissions where companyId = &#63; and primKey LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param primKey the prim key
	 */
	@Override
	public void removeByC_LikeP(long companyId, String primKey) {
		_collectionPersistenceFinderByC_LikeP.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, primKey});
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
		return _collectionPersistenceFinderByC_LikeP.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, primKey});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_N_S;

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

		return _collectionPersistenceFinderByC_N_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope}, start, end,
			orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_N_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope}, orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope}, orderByComparator);
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
		_collectionPersistenceFinderByC_N_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope});
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
		return _collectionPersistenceFinderByC_N_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_S_P;

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

		return _collectionPersistenceFinderByC_S_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, scope, primKey}, start, end,
			orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_S_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, scope, primKey}, orderByComparator);
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

		return _collectionPersistenceFinderByC_S_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, scope, primKey}, orderByComparator);
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
		_collectionPersistenceFinderByC_S_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, scope, primKey});
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
		return _collectionPersistenceFinderByC_S_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, scope, primKey});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_N_S_P;

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

		return _collectionPersistenceFinderByC_N_S_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, new String[] {primKey}},
			start, end, orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_N_S_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, new String[] {primKey}},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, new String[] {primKey}},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, ArrayUtil.sortedUnique(primKeys)
			},
			start, end, orderByComparator, useFinderCache);
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

		_collectionPersistenceFinderByC_N_S_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, new String[] {primKey}});
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

		return _collectionPersistenceFinderByC_N_S_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, new String[] {primKey}});
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

		return _collectionPersistenceFinderByC_N_S_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, ArrayUtil.sortedUnique(primKeys)
			});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_N_S_R;

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

		return _collectionPersistenceFinderByC_N_S_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, roleId}, start, end,
			orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_N_S_R.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, roleId}, orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S_R.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, roleId}, orderByComparator);
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

		_collectionPersistenceFinderByC_N_S_R.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, roleId});
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

		return _collectionPersistenceFinderByC_N_S_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, roleId});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_N_S_P_R;
	private UniquePersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_uniquePersistenceFinderByC_N_S_P_R;

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

		return _collectionPersistenceFinderByC_N_S_P_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, primKey, ArrayUtil.sortedUnique(roleIds)
			},
			start, end, orderByComparator, useFinderCache);
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

		return _uniquePersistenceFinderByC_N_S_P_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, primKey, roleId});
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

		return _uniquePersistenceFinderByC_N_S_P_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, scope, primKey, roleId},
			useFinderCache);
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

		return _collectionPersistenceFinderByC_N_S_P_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, primKey, new long[] {roleId}
			});
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

		return _collectionPersistenceFinderByC_N_S_P_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, primKey, ArrayUtil.sortedUnique(roleIds)
			});
	}

	private CollectionPersistenceFinder
		<ResourcePermission, NoSuchResourcePermissionException>
			_collectionPersistenceFinderByC_N_S_R_V;

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

		return _collectionPersistenceFinderByC_N_S_R_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, new long[] {roleId}, viewActionId
			},
			start, end, orderByComparator, useFinderCache);
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

		return _collectionPersistenceFinderByC_N_S_R_V.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, new long[] {roleId}, viewActionId
			},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S_R_V.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, new long[] {roleId}, viewActionId
			},
			orderByComparator);
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

		return _collectionPersistenceFinderByC_N_S_R_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, ArrayUtil.sortedUnique(roleIds),
				viewActionId
			},
			start, end, orderByComparator, useFinderCache);
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

		_collectionPersistenceFinderByC_N_S_R_V.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, new long[] {roleId}, viewActionId
			});
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

		return _collectionPersistenceFinderByC_N_S_R_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, new long[] {roleId}, viewActionId
			});
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

		return _collectionPersistenceFinderByC_N_S_R_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, name, scope, ArrayUtil.sortedUnique(roleIds),
				viewActionId
			});
	}

	public ResourcePermissionPersistenceImpl() {
		setModelClass(ResourcePermission.class);

		setModelImplClass(ResourcePermissionImpl.class);
		setModelPKClass(long.class);

		setTable(ResourcePermissionTable.INSTANCE);
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

		cacheUniqueFindersResult(resourcePermission, false);

		if (isNew) {
			resourcePermission.setNew(false);
		}

		resourcePermission.resetOriginalValues();

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

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
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
		_collectionPersistenceFinderByName = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByName",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, null),
			_SQL_SELECT_RESOURCEPERMISSION_WHERE,
			_SQL_COUNT_RESOURCEPERMISSION_WHERE,
			ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"resourcePermission.", "name", FinderColumn.Type.STRING, "=",
				true, true, ResourcePermission::getName));

		_collectionPersistenceFinderByScope = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByScope",
				new String[] {
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"scope"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByScope",
				new String[] {Integer.class.getName()}, new String[] {"scope"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByScope",
				new String[] {Integer.class.getName()}, new String[] {"scope"},
				false),
			_SQL_SELECT_RESOURCEPERMISSION_WHERE,
			_SQL_COUNT_RESOURCEPERMISSION_WHERE,
			ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new ArrayableFinderColumn<>(
				"resourcePermission.", "scope", FinderColumn.Type.INTEGER, "=",
				false, true, true, ResourcePermission::getScope));

		_collectionPersistenceFinderByRoleId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRoleId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, false),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"resourcePermission.", "roleId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getRoleId));

		_collectionPersistenceFinderByC_LikeP =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeP",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "primKey"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeP",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "primKey"}, false),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"resourcePermission.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getCompanyId),
				new FinderColumn<>(
					"resourcePermission.", "primKey", FinderColumn.Type.STRING,
					"LIKE", true, true, ResourcePermission::getPrimKey));

		_collectionPersistenceFinderByC_N_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "name", "scope"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "name", "scope"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "name", "scope"}, 0, 2, false, null),
			_SQL_SELECT_RESOURCEPERMISSION_WHERE,
			_SQL_COUNT_RESOURCEPERMISSION_WHERE,
			ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"resourcePermission.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ResourcePermission::getCompanyId),
			new FinderColumn<>(
				"resourcePermission.", "name", FinderColumn.Type.STRING, "=",
				true, true, ResourcePermission::getName),
			new FinderColumn<>(
				"resourcePermission.", "scope", FinderColumn.Type.INTEGER, "=",
				true, true, ResourcePermission::getScope));

		_collectionPersistenceFinderByC_S_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "scope", "primKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "scope", "primKey"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "scope", "primKey"}, 0, 4, false,
				null),
			_SQL_SELECT_RESOURCEPERMISSION_WHERE,
			_SQL_COUNT_RESOURCEPERMISSION_WHERE,
			ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"resourcePermission.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ResourcePermission::getCompanyId),
			new FinderColumn<>(
				"resourcePermission.", "scope", FinderColumn.Type.INTEGER, "=",
				true, true, ResourcePermission::getScope),
			new FinderColumn<>(
				"resourcePermission.", "primKey", FinderColumn.Type.STRING, "=",
				true, true, ResourcePermission::getPrimKey));

		_collectionPersistenceFinderByC_N_S_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name", "scope", "primKey"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), String.class.getName()
					},
					new String[] {"companyId", "name", "scope", "primKey"}, 0,
					10, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_P",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), String.class.getName()
					},
					new String[] {"companyId", "name", "scope", "primKey"}, 0,
					10, false, null),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"resourcePermission.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getCompanyId),
				new FinderColumn<>(
					"resourcePermission.", "name", FinderColumn.Type.STRING,
					"=", true, true, ResourcePermission::getName),
				new FinderColumn<>(
					"resourcePermission.", "scope", FinderColumn.Type.INTEGER,
					"=", true, true, ResourcePermission::getScope),
				new ArrayableFinderColumn<>(
					"resourcePermission.", "primKey", FinderColumn.Type.STRING,
					"=", false, true, true, ResourcePermission::getPrimKey));

		_collectionPersistenceFinderByC_N_S_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name", "scope", "roleId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_S_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {"companyId", "name", "scope", "roleId"}, 0, 2,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_S_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {"companyId", "name", "scope", "roleId"}, 0, 2,
					false, null),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"resourcePermission.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getCompanyId),
				new FinderColumn<>(
					"resourcePermission.", "name", FinderColumn.Type.STRING,
					"=", true, true, ResourcePermission::getName),
				new FinderColumn<>(
					"resourcePermission.", "scope", FinderColumn.Type.INTEGER,
					"=", true, true, ResourcePermission::getScope),
				new FinderColumn<>(
					"resourcePermission.", "roleId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getRoleId));

		_uniquePersistenceFinderByC_N_S_P_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N_S_P_R",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"companyId", "name", "scope", "primKey", "roleId"
				},
				0, 10, false, ResourcePermission::getCompanyId,
				convertNullFunction(ResourcePermission::getName),
				ResourcePermission::getScope,
				convertNullFunction(ResourcePermission::getPrimKey),
				ResourcePermission::getRoleId),
			_SQL_SELECT_RESOURCEPERMISSION_WHERE, "",
			new FinderColumn<>(
				"resourcePermission.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ResourcePermission::getCompanyId),
			new FinderColumn<>(
				"resourcePermission.", "name", FinderColumn.Type.STRING, "=",
				true, true, ResourcePermission::getName),
			new FinderColumn<>(
				"resourcePermission.", "scope", FinderColumn.Type.INTEGER, "=",
				true, true, ResourcePermission::getScope),
			new FinderColumn<>(
				"resourcePermission.", "primKey", FinderColumn.Type.STRING, "=",
				true, true, ResourcePermission::getPrimKey),
			new FinderColumn<>(
				"resourcePermission.", "roleId", FinderColumn.Type.LONG, "=",
				true, true, ResourcePermission::getRoleId));

		_collectionPersistenceFinderByC_N_S_P_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_P_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), String.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "name", "scope", "primKey", "roleId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_P_R",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), String.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "name", "scope", "primKey", "roleId"
					},
					0, 10, false, null),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", _uniquePersistenceFinderByC_N_S_P_R,
				new FinderColumn<>(
					"resourcePermission.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getCompanyId),
				new FinderColumn<>(
					"resourcePermission.", "name", FinderColumn.Type.STRING,
					"=", true, true, ResourcePermission::getName),
				new FinderColumn<>(
					"resourcePermission.", "scope", FinderColumn.Type.INTEGER,
					"=", true, true, ResourcePermission::getScope),
				new FinderColumn<>(
					"resourcePermission.", "primKey", FinderColumn.Type.STRING,
					"=", true, true, ResourcePermission::getPrimKey),
				new ArrayableFinderColumn<>(
					"resourcePermission.", "roleId", FinderColumn.Type.LONG,
					"=", false, true, true, ResourcePermission::getRoleId));

		_collectionPersistenceFinderByC_N_S_R_V =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_S_R_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "name", "scope", "roleId", "viewActionId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_N_S_R_V",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"companyId", "name", "scope", "roleId", "viewActionId"
					},
					0, 2, false, null),
				_SQL_SELECT_RESOURCEPERMISSION_WHERE,
				_SQL_COUNT_RESOURCEPERMISSION_WHERE,
				ResourcePermissionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"resourcePermission.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ResourcePermission::getCompanyId),
				new FinderColumn<>(
					"resourcePermission.", "name", FinderColumn.Type.STRING,
					"=", true, true, ResourcePermission::getName),
				new FinderColumn<>(
					"resourcePermission.", "scope", FinderColumn.Type.INTEGER,
					"=", true, true, ResourcePermission::getScope),
				new ArrayableFinderColumn<>(
					"resourcePermission.", "roleId", FinderColumn.Type.LONG,
					"=", false, true, true, ResourcePermission::getRoleId),
				new FinderColumn<>(
					"resourcePermission.", "viewActionId",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					ResourcePermission::isViewActionId));

		ResourcePermissionUtil.setPersistence(this);
	}

	public void destroy() {
		ResourcePermissionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ResourcePermissionImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ResourcePermissionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_RESOURCEPERMISSION =
		"SELECT resourcePermission FROM ResourcePermission resourcePermission";

	private static final String _SQL_SELECT_RESOURCEPERMISSION_WHERE =
		"SELECT resourcePermission FROM ResourcePermission resourcePermission WHERE ";

	private static final String _SQL_COUNT_RESOURCEPERMISSION_WHERE =
		"SELECT COUNT(resourcePermission) FROM ResourcePermission resourcePermission WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1955366497