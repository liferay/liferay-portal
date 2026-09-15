/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchLayoutBranchException;
import com.liferay.portal.kernel.model.LayoutBranch;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ProviderType
public interface LayoutBranchPersistence extends BasePersistence<LayoutBranch> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutBranchUtil} to access the layout branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public java.util.List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public LayoutBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
				orderByComparator)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public LayoutBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator);

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	public void removeByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching layout branches
	 */
	public int countByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns an ordered range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public java.util.List<LayoutBranch> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public LayoutBranch findByPlid_First(
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
				orderByComparator)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public LayoutBranch fetchByPlid_First(
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator);

	/**
	 * Removes all the layout branches where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public void removeByPlid(long plid);

	/**
	 * Returns the number of layout branches where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	public int countByPlid(long plid);

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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
	public java.util.List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public LayoutBranch findByL_P_First(
			long layoutSetBranchId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
				orderByComparator)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public LayoutBranch fetchByL_P_First(
		long layoutSetBranchId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator);

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 */
	public void removeByL_P(long layoutSetBranchId, long plid);

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	public int countByL_P(long layoutSetBranchId, long plid);

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public LayoutBranch findByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public LayoutBranch fetchByL_P_N(
		long layoutSetBranchId, long plid, String name, boolean useFinderCache);

	/**
	 * Removes the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the layout branch that was removed
	 */
	public LayoutBranch removeByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and name = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the number of matching layout branches
	 */
	public int countByL_P_N(long layoutSetBranchId, long plid, String name);

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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
	public java.util.List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutBranch findByL_P_M_First(
			long layoutSetBranchId, long plid, boolean master,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
				orderByComparator)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public LayoutBranch fetchByL_P_M_First(
		long layoutSetBranchId, long plid, boolean master,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator);

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 */
	public void removeByL_P_M(
		long layoutSetBranchId, long plid, boolean master);

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @return the number of matching layout branches
	 */
	public int countByL_P_M(long layoutSetBranchId, long plid, boolean master);

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	public LayoutBranch create(long layoutBranchId);

	/**
	 * Removes the layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch that was removed
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	public LayoutBranch remove(long layoutBranchId)
		throws NoSuchLayoutBranchException;

	public LayoutBranch updateImpl(LayoutBranch layoutBranch);

	/**
	 * Returns the layout branch with the primary key or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	public LayoutBranch findByPrimaryKey(long layoutBranchId)
		throws NoSuchLayoutBranchException;

	/**
	 * Returns the layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch, or <code>null</code> if a layout branch with the primary key could not be found
	 */
	public LayoutBranch fetchByPrimaryKey(long layoutBranchId);

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public default LayoutBranch fetchByL_P_N(
		long layoutSetBranchId, long plid, String name) {

		return fetchByL_P_N(layoutSetBranchId, plid, name, true);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId) {

		return findByLayoutSetBranchId(
			layoutSetBranchId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout branches where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByPlid(long plid) {
		return findByPlid(
			plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByPlid(
		long plid, int start, int end) {

		return findByPlid(plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator) {

		return findByPlid(plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid) {

		return findByL_P(
			layoutSetBranchId, plid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end) {

		return findByL_P(layoutSetBranchId, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator) {

		return findByL_P(
			layoutSetBranchId, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @return the matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master) {

		return findByL_P_M(
			layoutSetBranchId, plid, master,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end) {

		return findByL_P_M(
			layoutSetBranchId, plid, master, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public default java.util.List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutBranch>
			orderByComparator) {

		return findByL_P_M(
			layoutSetBranchId, plid, master, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:386074766