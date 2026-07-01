/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetBranchException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.LayoutSetBranchImpl;
import com.liferay.portal.model.impl.LayoutSetBranchModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the layout set branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutSetBranchPersistenceImpl
	extends BasePersistenceImpl<LayoutSetBranch, NoSuchLayoutSetBranchException>
	implements LayoutSetBranchPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSetBranchUtil</code> to access the layout set branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSetBranchImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<LayoutSetBranch, NoSuchLayoutSetBranchException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	@Override
	public List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch findByGroupId_First(
			long groupId, OrderByComparator<LayoutSetBranch> orderByComparator)
		throws NoSuchLayoutSetBranchException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch fetchByGroupId_First(
		long groupId, OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	@Override
	public List<LayoutSetBranch> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<LayoutSetBranch, NoSuchLayoutSetBranchException>
			_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	@Override
	public List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch findByG_P_First(
			long groupId, boolean privateLayout,
			OrderByComparator<LayoutSetBranch> orderByComparator)
		throws NoSuchLayoutSetBranchException {

		return _collectionPersistenceFinderByG_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch fetchByG_P_First(
		long groupId, boolean privateLayout,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	@Override
	public List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	@Override
	public void removeByG_P(long groupId, boolean privateLayout) {
		_collectionPersistenceFinderByG_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches
	 */
	@Override
	public int countByG_P(long groupId, boolean privateLayout) {
		return _collectionPersistenceFinderByG_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, boolean privateLayout) {
		return _collectionPersistenceFinderByG_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, groupId);
	}

	private UniquePersistenceFinder
		<LayoutSetBranch, NoSuchLayoutSetBranchException>
			_uniquePersistenceFinderByG_P_N;

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch findByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws NoSuchLayoutSetBranchException {

		return _uniquePersistenceFinderByG_P_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, name});
	}

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch fetchByG_P_N(
		long groupId, boolean privateLayout, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, name}, useFinderCache);
	}

	/**
	 * Removes the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the layout set branch that was removed
	 */
	@Override
	public LayoutSetBranch removeByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws NoSuchLayoutSetBranchException {

		LayoutSetBranch layoutSetBranch = findByG_P_N(
			groupId, privateLayout, name);

		return remove(layoutSetBranch);
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the number of matching layout set branches
	 */
	@Override
	public int countByG_P_N(long groupId, boolean privateLayout, String name) {
		return _uniquePersistenceFinderByG_P_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, name});
	}

	private FilterCollectionPersistenceFinder
		<LayoutSetBranch, NoSuchLayoutSetBranchException>
			_collectionPersistenceFinderByG_P_M;

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	@Override
	public List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_M.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch findByG_P_M_First(
			long groupId, boolean privateLayout, boolean master,
			OrderByComparator<LayoutSetBranch> orderByComparator)
		throws NoSuchLayoutSetBranchException {

		return _collectionPersistenceFinderByG_P_M.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master}, orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	@Override
	public LayoutSetBranch fetchByG_P_M_First(
		long groupId, boolean privateLayout, boolean master,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	@Override
	public List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByG_P_M.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 */
	@Override
	public void removeByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		_collectionPersistenceFinderByG_P_M.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master});
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches
	 */
	@Override
	public int countByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return _collectionPersistenceFinderByG_P_M.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master});
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return _collectionPersistenceFinderByG_P_M.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, master}, groupId);
	}

	public LayoutSetBranchPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutSetBranch.class);

		setModelImplClass(LayoutSetBranchImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSetBranchTable.INSTANCE);
	}

	/**
	 * Creates a new layout set branch with the primary key. Does not add the layout set branch to the database.
	 *
	 * @param layoutSetBranchId the primary key for the new layout set branch
	 * @return the new layout set branch
	 */
	@Override
	public LayoutSetBranch create(long layoutSetBranchId) {
		LayoutSetBranch layoutSetBranch = new LayoutSetBranchImpl();

		layoutSetBranch.setNew(true);
		layoutSetBranch.setPrimaryKey(layoutSetBranchId);

		layoutSetBranch.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutSetBranch;
	}

	/**
	 * Removes the layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch that was removed
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	@Override
	public LayoutSetBranch remove(long layoutSetBranchId)
		throws NoSuchLayoutSetBranchException {

		return remove((Serializable)layoutSetBranchId);
	}

	@Override
	protected LayoutSetBranch removeImpl(LayoutSetBranch layoutSetBranch) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSetBranch)) {
				layoutSetBranch = (LayoutSetBranch)session.get(
					LayoutSetBranchImpl.class,
					layoutSetBranch.getPrimaryKeyObj());
			}

			if (layoutSetBranch != null) {
				session.delete(layoutSetBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSetBranch != null) {
			clearCache(layoutSetBranch);
		}

		return layoutSetBranch;
	}

	@Override
	public LayoutSetBranch updateImpl(LayoutSetBranch layoutSetBranch) {
		boolean isNew = layoutSetBranch.isNew();

		if (!(layoutSetBranch instanceof LayoutSetBranchModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutSetBranch.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutSetBranch);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSetBranch proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSetBranch implementation " +
					layoutSetBranch.getClass());
		}

		LayoutSetBranchModelImpl layoutSetBranchModelImpl =
			(LayoutSetBranchModelImpl)layoutSetBranch;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutSetBranch.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutSetBranch.setCreateDate(date);
			}
			else {
				layoutSetBranch.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutSetBranchModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutSetBranch.setModifiedDate(date);
			}
			else {
				layoutSetBranch.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(layoutSetBranch);
			}
			else {
				layoutSetBranch = (LayoutSetBranch)session.merge(
					layoutSetBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSetBranch, false);

		if (isNew) {
			layoutSetBranch.setNew(false);
		}

		layoutSetBranch.resetOriginalValues();

		return layoutSetBranch;
	}

	/**
	 * Returns the layout set branch with the primary key or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	@Override
	public LayoutSetBranch findByPrimaryKey(long layoutSetBranchId)
		throws NoSuchLayoutSetBranchException {

		return findByPrimaryKey((Serializable)layoutSetBranchId);
	}

	/**
	 * Returns the layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch, or <code>null</code> if a layout set branch with the primary key could not be found
	 */
	@Override
	public LayoutSetBranch fetchByPrimaryKey(long layoutSetBranchId) {
		return fetchByPrimaryKey((Serializable)layoutSetBranchId);
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
		return "layoutSetBranchId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSETBRANCH;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LayoutSetBranchModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the layout set branch persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_LAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_LAYOUTSETBRANCH_WHERE,
				LayoutSetBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetBranch.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LayoutSetBranch::getGroupId));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, false),
				_SQL_SELECT_LAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_LAYOUTSETBRANCH_WHERE,
				LayoutSetBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetBranch.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LayoutSetBranch::getGroupId),
				new FinderColumn<>(
					"layoutSetBranch.", "privateLayout",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutSetBranch::isPrivateLayout));

		_uniquePersistenceFinderByG_P_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_N",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "privateLayout", "name"}, 0, 4, false,
				LayoutSetBranch::getGroupId, LayoutSetBranch::isPrivateLayout,
				convertNullFunction(LayoutSetBranch::getName)),
			_SQL_SELECT_LAYOUTSETBRANCH_WHERE, "",
			new FinderColumn<>(
				"layoutSetBranch.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutSetBranch::getGroupId),
			new FinderColumn<>(
				"layoutSetBranch.", "privateLayout", FinderColumn.Type.BOOLEAN,
				"=", true, true, LayoutSetBranch::isPrivateLayout),
			new FinderColumn<>(
				"layoutSetBranch.", "name", FinderColumn.Type.STRING, "=", true,
				true, LayoutSetBranch::getName));

		_collectionPersistenceFinderByG_P_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout", "master"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout", "master"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_M",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout", "master"}, false),
				_SQL_SELECT_LAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_LAYOUTSETBRANCH_WHERE,
				LayoutSetBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetBranch.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, LayoutSetBranch::getGroupId),
				new FinderColumn<>(
					"layoutSetBranch.", "privateLayout",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutSetBranch::isPrivateLayout),
				new FinderColumn<>(
					"layoutSetBranch.", "master", FinderColumn.Type.BOOLEAN,
					"=", true, true, LayoutSetBranch::isMaster));

		LayoutSetBranchUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutSetBranchUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutSetBranchImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutSetBranchModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSETBRANCH =
		"SELECT layoutSetBranch FROM LayoutSetBranch layoutSetBranch";

	private static final String _SQL_SELECT_LAYOUTSETBRANCH_WHERE =
		"SELECT layoutSetBranch FROM LayoutSetBranch layoutSetBranch WHERE ";

	private static final String _SQL_COUNT_LAYOUTSETBRANCH_WHERE =
		"SELECT COUNT(layoutSetBranch) FROM LayoutSetBranch layoutSetBranch WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutSetBranch exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetBranchPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-275391579