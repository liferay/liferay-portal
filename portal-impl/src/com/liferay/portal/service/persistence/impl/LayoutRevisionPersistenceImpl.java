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
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchLayoutRevisionException;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.LayoutRevisionImpl;
import com.liferay.portal.model.impl.LayoutRevisionModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the layout revision service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutRevisionPersistenceImpl
	extends BasePersistenceImpl<LayoutRevision, NoSuchLayoutRevisionException>
	implements LayoutRevisionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutRevisionUtil</code> to access the layout revision persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutRevisionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByLayoutSetBranchId;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutSetBranchId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByLayoutSetBranchId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByLayoutSetBranchId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	@Override
	public void removeByLayoutSetBranchId(long layoutSetBranchId) {
		_collectionPersistenceFinderByLayoutSetBranchId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByLayoutSetBranchId(long layoutSetBranchId) {
		return _collectionPersistenceFinderByLayoutSetBranchId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByPlid_First(
			long plid, OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByPlid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByPlid_First(
		long plid, OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	/**
	 * Returns the number of layout revisions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByStatus;

	/**
	 * Returns an ordered range of all the layout revisions where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByStatus(
		int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStatus.find(
			FinderCacheUtil.getFinderCache(), new Object[] {status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByStatus_First(
			int status, OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByStatus.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {status},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByStatus_First(
		int status, OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByStatus.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {status},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where status = &#63; from the database.
	 *
	 * @param status the status
	 */
	@Override
	public void removeByStatus(int status) {
		_collectionPersistenceFinderByStatus.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {status});
	}

	/**
	 * Returns the number of layout revisions where status = &#63;.
	 *
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByStatus(int status) {
		return _collectionPersistenceFinderByStatus.count(
			FinderCacheUtil.getFinderCache(), new Object[] {status});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_H;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_H(
		long layoutSetBranchId, boolean head, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_H.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_H_First(
			long layoutSetBranchId, boolean head,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_H.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_H_First(
		long layoutSetBranchId, boolean head,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_H.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 */
	@Override
	public void removeByL_H(long layoutSetBranchId, boolean head) {
		_collectionPersistenceFinderByL_H.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_H(long layoutSetBranchId, boolean head) {
		return _collectionPersistenceFinderByL_H.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_P;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_P_First(
			long layoutSetBranchId, long plid,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_P_First(
		long layoutSetBranchId, long plid,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; from the database.
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
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_P(long layoutSetBranchId, long plid) {
		return _collectionPersistenceFinderByL_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_S;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_S(
		long layoutSetBranchId, int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_S_First(
			long layoutSetBranchId, int status,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, status}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_S_First(
		long layoutSetBranchId, int status,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, status}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 */
	@Override
	public void removeByL_S(long layoutSetBranchId, int status) {
		_collectionPersistenceFinderByL_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, status});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_S(long layoutSetBranchId, int status) {
		return _collectionPersistenceFinderByL_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, status});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByH_P;

	/**
	 * Returns an ordered range of all the layout revisions where head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByH_P(
		boolean head, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByH_P.find(
			FinderCacheUtil.getFinderCache(), new Object[] {head, plid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByH_P_First(
			boolean head, long plid,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByH_P.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {head, plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByH_P_First(
		boolean head, long plid,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByH_P.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {head, plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where head = &#63; and plid = &#63; from the database.
	 *
	 * @param head the head
	 * @param plid the plid
	 */
	@Override
	public void removeByH_P(boolean head, long plid) {
		_collectionPersistenceFinderByH_P.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {head, plid});
	}

	/**
	 * Returns the number of layout revisions where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByH_P(boolean head, long plid) {
		return _collectionPersistenceFinderByH_P.count(
			FinderCacheUtil.getFinderCache(), new Object[] {head, plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByP_NotS;

	/**
	 * Returns all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByP_NotS(long plid, int status) {
		return findByP_NotS(
			plid, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end) {

		return findByP_NotS(plid, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return findByP_NotS(plid, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_NotS.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByP_NotS_First(
			long plid, int status,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByP_NotS.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, status},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByP_NotS_First(
		long plid, int status,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByP_NotS.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, status},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where plid = &#63; and status &ne; &#63; from the database.
	 *
	 * @param plid the plid
	 * @param status the status
	 */
	@Override
	public void removeByP_NotS(long plid, int status) {
		_collectionPersistenceFinderByP_NotS.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, status});
	}

	/**
	 * Returns the number of layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByP_NotS(long plid, int status) {
		return _collectionPersistenceFinderByP_NotS.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, status});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_L_P;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_L_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, layoutBranchId, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_L_P_First(
			long layoutSetBranchId, long layoutBranchId, long plid,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_L_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, layoutBranchId, plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_L_P_First(
		long layoutSetBranchId, long layoutBranchId, long plid,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_L_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, layoutBranchId, plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 */
	@Override
	public void removeByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		_collectionPersistenceFinderByL_L_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, layoutBranchId, plid});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return _collectionPersistenceFinderByL_L_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, layoutBranchId, plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_P_P;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		int start, int end, OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_P_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, parentLayoutRevisionId, plid},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_P_P_First(
			long layoutSetBranchId, long parentLayoutRevisionId, long plid,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_P_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, parentLayoutRevisionId, plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_P_P_First(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_P_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, parentLayoutRevisionId, plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 */
	@Override
	public void removeByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		_collectionPersistenceFinderByL_P_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, parentLayoutRevisionId, plid});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		return _collectionPersistenceFinderByL_P_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, parentLayoutRevisionId, plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_H_P_Collection;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_H_P_Collection.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_H_P_Collection_First(
			long layoutSetBranchId, boolean head, long plid,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_H_P_Collection.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, plid}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_H_P_Collection_First(
		long layoutSetBranchId, boolean head, long plid,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_H_P_Collection.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, plid}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 */
	@Override
	public void removeByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid) {

		_collectionPersistenceFinderByL_H_P_Collection.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, plid});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid) {

		return _collectionPersistenceFinderByL_H_P_Collection.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, plid});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_H_S;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_H_S(
		long layoutSetBranchId, boolean head, int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_H_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_H_S_First(
			long layoutSetBranchId, boolean head, int status,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_H_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, status}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_H_S_First(
		long layoutSetBranchId, boolean head, int status,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_H_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, status}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 */
	@Override
	public void removeByL_H_S(
		long layoutSetBranchId, boolean head, int status) {

		_collectionPersistenceFinderByL_H_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, status});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_H_S(long layoutSetBranchId, boolean head, int status) {
		return _collectionPersistenceFinderByL_H_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, head, status});
	}

	private CollectionPersistenceFinder
		<LayoutRevision, NoSuchLayoutRevisionException>
			_collectionPersistenceFinderByL_P_S;

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	@Override
	public List<LayoutRevision> findByL_P_S(
		long layoutSetBranchId, long plid, int status, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByL_P_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision findByL_P_S_First(
			long layoutSetBranchId, long plid, int status,
			OrderByComparator<LayoutRevision> orderByComparator)
		throws NoSuchLayoutRevisionException {

		return _collectionPersistenceFinderByL_P_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, status}, orderByComparator);
	}

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	@Override
	public LayoutRevision fetchByL_P_S_First(
		long layoutSetBranchId, long plid, int status,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByL_P_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, status}, orderByComparator);
	}

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 */
	@Override
	public void removeByL_P_S(long layoutSetBranchId, long plid, int status) {
		_collectionPersistenceFinderByL_P_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, status});
	}

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	@Override
	public int countByL_P_S(long layoutSetBranchId, long plid, int status) {
		return _collectionPersistenceFinderByL_P_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetBranchId, plid, status});
	}

	public LayoutRevisionPersistenceImpl() {
		setModelClass(LayoutRevision.class);

		setModelImplClass(LayoutRevisionImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutRevisionTable.INSTANCE);
	}

	/**
	 * Creates a new layout revision with the primary key. Does not add the layout revision to the database.
	 *
	 * @param layoutRevisionId the primary key for the new layout revision
	 * @return the new layout revision
	 */
	@Override
	public LayoutRevision create(long layoutRevisionId) {
		LayoutRevision layoutRevision = new LayoutRevisionImpl();

		layoutRevision.setNew(true);
		layoutRevision.setPrimaryKey(layoutRevisionId);

		layoutRevision.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutRevision;
	}

	/**
	 * Removes the layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision that was removed
	 * @throws NoSuchLayoutRevisionException if a layout revision with the primary key could not be found
	 */
	@Override
	public LayoutRevision remove(long layoutRevisionId)
		throws NoSuchLayoutRevisionException {

		return remove((Serializable)layoutRevisionId);
	}

	@Override
	protected LayoutRevision removeImpl(LayoutRevision layoutRevision) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutRevision)) {
				layoutRevision = (LayoutRevision)session.get(
					LayoutRevisionImpl.class,
					layoutRevision.getPrimaryKeyObj());
			}

			if (layoutRevision != null) {
				session.delete(layoutRevision);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutRevision != null) {
			clearCache(layoutRevision);
		}

		return layoutRevision;
	}

	@Override
	public LayoutRevision updateImpl(LayoutRevision layoutRevision) {
		boolean isNew = layoutRevision.isNew();

		if (!(layoutRevision instanceof LayoutRevisionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutRevision.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutRevision);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutRevision proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutRevision implementation " +
					layoutRevision.getClass());
		}

		LayoutRevisionModelImpl layoutRevisionModelImpl =
			(LayoutRevisionModelImpl)layoutRevision;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutRevision.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutRevision.setCreateDate(date);
			}
			else {
				layoutRevision.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutRevisionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutRevision.setModifiedDate(date);
			}
			else {
				layoutRevision.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(layoutRevision);
			}
			else {
				layoutRevision = (LayoutRevision)session.merge(layoutRevision);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutRevision, false);

		if (isNew) {
			layoutRevision.setNew(false);
		}

		layoutRevision.resetOriginalValues();

		return layoutRevision;
	}

	/**
	 * Returns the layout revision with the primary key or throws a <code>NoSuchLayoutRevisionException</code> if it could not be found.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision
	 * @throws NoSuchLayoutRevisionException if a layout revision with the primary key could not be found
	 */
	@Override
	public LayoutRevision findByPrimaryKey(long layoutRevisionId)
		throws NoSuchLayoutRevisionException {

		return findByPrimaryKey((Serializable)layoutRevisionId);
	}

	/**
	 * Returns the layout revision with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision, or <code>null</code> if a layout revision with the primary key could not be found
	 */
	@Override
	public LayoutRevision fetchByPrimaryKey(long layoutRevisionId) {
		return fetchByPrimaryKey((Serializable)layoutRevisionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "layoutRevisionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTREVISION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LayoutRevisionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the layout revision persistence.
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
				_SQL_SELECT_LAYOUTREVISION_WHERE,
				_SQL_COUNT_LAYOUTREVISION_WHERE,
				LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutRevision.", "layoutSetBranchId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutRevision::getLayoutSetBranchId));

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
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByStatus =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByStatus",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByStatus",
					new String[] {Integer.class.getName()},
					new String[] {"status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByStatus",
					new String[] {Integer.class.getName()},
					new String[] {"status"}, false),
				_SQL_SELECT_LAYOUTREVISION_WHERE,
				_SQL_COUNT_LAYOUTREVISION_WHERE,
				LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutRevision.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, LayoutRevision::getStatus));

		_collectionPersistenceFinderByL_H = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_H",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_H",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"layoutSetBranchId", "head"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_H",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"layoutSetBranchId", "head"}, false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, LayoutRevision::isHead));

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
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByL_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"layoutSetBranchId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"layoutSetBranchId", "status"}, false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, LayoutRevision::getStatus));

		_collectionPersistenceFinderByH_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByH_P",
				new String[] {
					Boolean.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"head", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByH_P",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"head", "plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByH_P",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"head", "plid"}, false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, LayoutRevision::isHead),
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByP_NotS =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"plid", "status"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_NotS",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"plid", "status"}, false),
				_SQL_SELECT_LAYOUTREVISION_WHERE,
				_SQL_COUNT_LAYOUTREVISION_WHERE,
				LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutRevision.", "plid", FinderColumn.Type.LONG, "=",
					true, true, LayoutRevision::getPlid),
				new FinderColumn<>(
					"layoutRevision.", "status", FinderColumn.Type.INTEGER,
					"!=", true, true, LayoutRevision::getStatus));

		_collectionPersistenceFinderByL_L_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_L_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "layoutBranchId", "plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_L_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"layoutSetBranchId", "layoutBranchId", "plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_L_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"layoutSetBranchId", "layoutBranchId", "plid"},
				false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"layoutRevision.status != 5", "layoutRevision.status != 5",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "layoutBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutBranchId),
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByL_P_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_P_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"layoutSetBranchId", "parentLayoutRevisionId", "plid"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_P_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"layoutSetBranchId", "parentLayoutRevisionId", "plid"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_P_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"layoutSetBranchId", "parentLayoutRevisionId", "plid"
				},
				false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "parentLayoutRevisionId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutRevision::getParentLayoutRevisionId),
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByL_H_P_Collection =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByL_H_P_Collection",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetBranchId", "head", "plid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByL_H_P_Collection",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName()
					},
					new String[] {"layoutSetBranchId", "head", "plid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByL_H_P_Collection",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName()
					},
					new String[] {"layoutSetBranchId", "head", "plid"}, false),
				_SQL_SELECT_LAYOUTREVISION_WHERE,
				_SQL_COUNT_LAYOUTREVISION_WHERE,
				LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"layoutRevision.", "layoutSetBranchId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutRevision::getLayoutSetBranchId),
				new FinderColumn<>(
					"layoutRevision.", "head", FinderColumn.Type.BOOLEAN, "=",
					true, true, LayoutRevision::isHead),
				new FinderColumn<>(
					"layoutRevision.", "plid", FinderColumn.Type.LONG, "=",
					true, true, LayoutRevision::getPlid));

		_collectionPersistenceFinderByL_H_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "head", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"layoutSetBranchId", "head", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_H_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName()
				},
				new String[] {"layoutSetBranchId", "head", "status"}, false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "head", FinderColumn.Type.BOOLEAN, "=", true,
				true, LayoutRevision::isHead),
			new FinderColumn<>(
				"layoutRevision.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, LayoutRevision::getStatus));

		_collectionPersistenceFinderByL_P_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByL_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByL_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByL_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"layoutSetBranchId", "plid", "status"}, false),
			_SQL_SELECT_LAYOUTREVISION_WHERE, _SQL_COUNT_LAYOUTREVISION_WHERE,
			LayoutRevisionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutRevision.", "layoutSetBranchId", FinderColumn.Type.LONG,
				"=", true, true, LayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"layoutRevision.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutRevision::getPlid),
			new FinderColumn<>(
				"layoutRevision.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, LayoutRevision::getStatus));

		LayoutRevisionUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutRevisionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutRevisionImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutRevisionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTREVISION =
		"SELECT layoutRevision FROM LayoutRevision layoutRevision";

	private static final String _SQL_SELECT_LAYOUTREVISION_WHERE =
		"SELECT layoutRevision FROM LayoutRevision layoutRevision WHERE ";

	private static final String _SQL_COUNT_LAYOUTREVISION_WHERE =
		"SELECT COUNT(layoutRevision) FROM LayoutRevision layoutRevision WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutRevision exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:130722214