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
import com.liferay.portal.kernel.exception.NoSuchLayoutBranchException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutBranchTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutBranchPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutBranchUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.LayoutBranchImpl;
import com.liferay.portal.model.impl.LayoutBranchModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the layout branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutBranchPersistenceImpl
	extends BasePersistenceImpl<LayoutBranch, NoSuchLayoutBranchException>
	implements LayoutBranchPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutBranchUtil</code> to access the layout branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutBranchImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutBranch, NoSuchLayoutBranchException>
			_collectionPersistenceFinderByLayoutSetBranchId;

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	@Override
	public List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutSetBranchId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws NoSuchLayoutBranchException {

		return _collectionPersistenceFinderByLayoutSetBranchId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByLayoutSetBranchId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	@Override
	public void removeByLayoutSetBranchId(long layoutSetBranchId) {
		_collectionPersistenceFinderByLayoutSetBranchId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching layout branches
	 */
	@Override
	public int countByLayoutSetBranchId(long layoutSetBranchId) {
		return _collectionPersistenceFinderByLayoutSetBranchId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	private CollectionPersistenceFinder
		<LayoutBranch, NoSuchLayoutBranchException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	@Override
	public List<LayoutBranch> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch findByPlid_First(
			long plid, OrderByComparator<LayoutBranch> orderByComparator)
		throws NoSuchLayoutBranchException {

		return _collectionPersistenceFinderByPlid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch fetchByPlid_First(
		long plid, OrderByComparator<LayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout branches where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	/**
	 * Returns the number of layout branches where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutBranch, NoSuchLayoutBranchException>
			_collectionPersistenceFinderByL_P;

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	@Override
	public List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch findByL_P_First(
			long layoutSetBranchId, long plid,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws NoSuchLayoutBranchException {

		return _collectionPersistenceFinderByL_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch fetchByL_P_First(
		long layoutSetBranchId, long plid,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByL_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 */
	@Override
	public void removeByL_P(long layoutSetBranchId, long plid) {
		_collectionPersistenceFinderByL_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid});
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	@Override
	public int countByL_P(long layoutSetBranchId, long plid) {
		return _collectionPersistenceFinderByL_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid});
	}

	private UniquePersistenceFinder<LayoutBranch, NoSuchLayoutBranchException>
		_uniquePersistenceFinderByL_P_N;

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch findByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws NoSuchLayoutBranchException {

		return _uniquePersistenceFinderByL_P_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, name});
	}

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch fetchByL_P_N(
		long layoutSetBranchId, long plid, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByL_P_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, name}, useFinderCache);
	}

	/**
	 * Removes the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the layout branch that was removed
	 */
	@Override
	public LayoutBranch removeByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws NoSuchLayoutBranchException {

		LayoutBranch layoutBranch = findByL_P_N(layoutSetBranchId, plid, name);

		return remove(layoutBranch);
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and name = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the number of matching layout branches
	 */
	@Override
	public int countByL_P_N(long layoutSetBranchId, long plid, String name) {
		return _uniquePersistenceFinderByL_P_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, name});
	}

	private CollectionPersistenceFinder
		<LayoutBranch, NoSuchLayoutBranchException>
			_collectionPersistenceFinderByL_P_M;

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	@Override
	public List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_P_M.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, master}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch findByL_P_M_First(
			long layoutSetBranchId, long plid, boolean master,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws NoSuchLayoutBranchException {

		return _collectionPersistenceFinderByL_P_M.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, master}, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	@Override
	public LayoutBranch fetchByL_P_M_First(
		long layoutSetBranchId, long plid, boolean master,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByL_P_M.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, master}, orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 */
	@Override
	public void removeByL_P_M(
		long layoutSetBranchId, long plid, boolean master) {

		_collectionPersistenceFinderByL_P_M.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, master});
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @return the number of matching layout branches
	 */
	@Override
	public int countByL_P_M(long layoutSetBranchId, long plid, boolean master) {
		return _collectionPersistenceFinderByL_P_M.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, master});
	}

	public LayoutBranchPersistenceImpl() {
		setModelClass(LayoutBranch.class);

		setModelImplClass(LayoutBranchImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutBranchTable.INSTANCE);
	}

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	@Override
	public LayoutBranch create(long layoutBranchId) {
		LayoutBranch layoutBranch = new LayoutBranchImpl();

		layoutBranch.setNew(true);
		layoutBranch.setPrimaryKey(layoutBranchId);

		layoutBranch.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutBranch;
	}

	/**
	 * Removes the layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch that was removed
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	@Override
	public LayoutBranch remove(long layoutBranchId)
		throws NoSuchLayoutBranchException {

		return remove((Serializable)layoutBranchId);
	}

	@Override
	protected LayoutBranch removeImpl(LayoutBranch layoutBranch) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutBranch)) {
				layoutBranch = (LayoutBranch)session.get(
					LayoutBranchImpl.class, layoutBranch.getPrimaryKeyObj());
			}

			if (layoutBranch != null) {
				session.delete(layoutBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutBranch != null) {
			clearCache(layoutBranch);
		}

		return layoutBranch;
	}

	@Override
	public LayoutBranch updateImpl(LayoutBranch layoutBranch) {
		boolean isNew = layoutBranch.isNew();

		if (!(layoutBranch instanceof LayoutBranchModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutBranch.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutBranch);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutBranch proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutBranch implementation " +
					layoutBranch.getClass());
		}

		LayoutBranchModelImpl layoutBranchModelImpl =
			(LayoutBranchModelImpl)layoutBranch;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(layoutBranch);
			}
			else {
				layoutBranch = (LayoutBranch)session.merge(layoutBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutBranch, false);

		if (isNew) {
			layoutBranch.setNew(false);
		}

		layoutBranch.resetOriginalValues();

		return layoutBranch;
	}

	/**
	 * Returns the layout branch with the primary key or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	@Override
	public LayoutBranch findByPrimaryKey(long layoutBranchId)
		throws NoSuchLayoutBranchException {

		return findByPrimaryKey((Serializable)layoutBranchId);
	}

	/**
	 * Returns the layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch, or <code>null</code> if a layout branch with the primary key could not be found
	 */
	@Override
	public LayoutBranch fetchByPrimaryKey(long layoutBranchId) {
		return fetchByPrimaryKey((Serializable)layoutBranchId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "layoutBranchId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTBRANCH;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LayoutBranchModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the layout branch persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByLayoutSetBranchId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutSetBranchId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutSetBranchId",
					new String[] {Long.class.getName()},
					new String[] {"layoutSetBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutSetBranchId",
					new String[] {Long.class.getName()},
					new String[] {"layoutSetBranchId"}, false),
				_SQL_SELECT_LAYOUTBRANCH_WHERE, _SQL_COUNT_LAYOUTBRANCH_WHERE,
				LayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutBranch.", "layoutSetBranchId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutBranch::getLayoutSetBranchId));

		_collectionPersistenceFinderByPlid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				false),
			_SQL_SELECT_LAYOUTBRANCH_WHERE, _SQL_COUNT_LAYOUTBRANCH_WHERE,
			LayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutBranch.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutBranch::getPlid));

		_collectionPersistenceFinderByL_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"layoutSetBranchId", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"layoutSetBranchId", "plid"}, false),
			_SQL_SELECT_LAYOUTBRANCH_WHERE, _SQL_COUNT_LAYOUTBRANCH_WHERE,
			LayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutBranch.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutBranch::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutBranch.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutBranch::getPlid));

		_uniquePersistenceFinderByL_P_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByL_P_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "name"}, 0, 4, false,
				LayoutBranch::getLayoutSetBranchId, LayoutBranch::getPlid,
				convertNullFunction(LayoutBranch::getName)),
			_SQL_SELECT_LAYOUTBRANCH_WHERE, "",
			new FinderColumn<>(
				"layoutBranch.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutBranch::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutBranch.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutBranch::getPlid),
			new FinderColumn<>(
				"layoutBranch.", "name", FinderColumn.Type.STRING, "=", true,
				true, LayoutBranch::getName));

		_collectionPersistenceFinderByL_P_M = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_P_M",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "master"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_P_M",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "master"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_P_M",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "master"}, false),
			_SQL_SELECT_LAYOUTBRANCH_WHERE, _SQL_COUNT_LAYOUTBRANCH_WHERE,
			LayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutBranch.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutBranch::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutBranch.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutBranch::getPlid),
			new FinderColumn<>(
				"layoutBranch.", "master", FinderColumn.Type.BOOLEAN, "=", true,
				true, LayoutBranch::isMaster));

		LayoutBranchUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutBranchUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutBranchImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutBranchModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTBRANCH =
		"SELECT layoutBranch FROM LayoutBranch layoutBranch";

	private static final String _SQL_SELECT_LAYOUTBRANCH_WHERE =
		"SELECT layoutBranch FROM LayoutBranch layoutBranch WHERE ";

	private static final String _SQL_COUNT_LAYOUTBRANCH_WHERE =
		"SELECT COUNT(layoutBranch) FROM LayoutBranch layoutBranch WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutBranch exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutBranchPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1553892618