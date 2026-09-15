/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchLayoutRevisionException;
import com.liferay.portal.kernel.model.LayoutRevision;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout revision service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevisionUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ProviderType
public interface LayoutRevisionPersistence
	extends BasePersistence<LayoutRevision> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutRevisionUtil} to access the layout revision persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	public void removeByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching layout revisions
	 */
	public int countByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByPlid_First(
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByPlid_First(
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public void removeByPlid(long plid);

	/**
	 * Returns the number of layout revisions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByPlid(long plid);

	/**
	 * Returns an ordered range of all the layout revisions where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByStatus(
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByStatus_First(
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByStatus_First(
		int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where status = &#63; from the database.
	 *
	 * @param status the status
	 */
	public void removeByStatus(int status);

	/**
	 * Returns the number of layout revisions where status = &#63;.
	 *
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	public int countByStatus(int status);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_H(
		long layoutSetBranchId, boolean head, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByL_H_First(
			long layoutSetBranchId, boolean head,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_H_First(
		long layoutSetBranchId, boolean head,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 */
	public void removeByL_H(long layoutSetBranchId, boolean head);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @return the number of matching layout revisions
	 */
	public int countByL_H(long layoutSetBranchId, boolean head);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByL_P_First(
			long layoutSetBranchId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_P_First(
		long layoutSetBranchId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 */
	public void removeByL_P(long layoutSetBranchId, long plid);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByL_P(long layoutSetBranchId, long plid);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_S(
		long layoutSetBranchId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByL_S_First(
			long layoutSetBranchId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_S_First(
		long layoutSetBranchId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 */
	public void removeByL_S(long layoutSetBranchId, int status);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	public int countByL_S(long layoutSetBranchId, int status);

	/**
	 * Returns an ordered range of all the layout revisions where head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByH_P(
		boolean head, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByH_P_First(
			boolean head, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByH_P_First(
		boolean head, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where head = &#63; and plid = &#63; from the database.
	 *
	 * @param head the head
	 * @param plid the plid
	 */
	public void removeByH_P(boolean head, long plid);

	/**
	 * Returns the number of layout revisions where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByH_P(boolean head, long plid);

	/**
	 * Returns all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByP_NotS(long plid, int status);

	/**
	 * Returns a range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public java.util.List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByP_NotS(
		long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision
	 * @throws NoSuchLayoutRevisionException if a matching layout revision could not be found
	 */
	public LayoutRevision findByP_NotS_First(
			long plid, int status,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByP_NotS_First(
		long plid, int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where plid = &#63; and status &ne; &#63; from the database.
	 *
	 * @param plid the plid
	 * @param status the status
	 */
	public void removeByP_NotS(long plid, int status);

	/**
	 * Returns the number of layout revisions where plid = &#63; and status &ne; &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	public int countByP_NotS(long plid, int status);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutRevision findByL_L_P_First(
			long layoutSetBranchId, long layoutBranchId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_L_P_First(
		long layoutSetBranchId, long layoutBranchId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 */
	public void removeByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutRevision findByL_P_P_First(
			long layoutSetBranchId, long parentLayoutRevisionId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_P_P_First(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 */
	public void removeByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutRevision findByL_H_P_Collection_First(
			long layoutSetBranchId, boolean head, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_H_P_Collection_First(
		long layoutSetBranchId, boolean head, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 */
	public void removeByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @return the number of matching layout revisions
	 */
	public int countByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_H_S(
		long layoutSetBranchId, boolean head, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutRevision findByL_H_S_First(
			long layoutSetBranchId, boolean head, int status,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_H_S_First(
		long layoutSetBranchId, boolean head, int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 */
	public void removeByL_H_S(long layoutSetBranchId, boolean head, int status);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	public int countByL_H_S(long layoutSetBranchId, boolean head, int status);

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	public java.util.List<LayoutRevision> findByL_P_S(
		long layoutSetBranchId, long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator,
		boolean useFinderCache);

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
	public LayoutRevision findByL_P_S_First(
			long layoutSetBranchId, long plid, int status,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
				orderByComparator)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the first layout revision in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout revision, or <code>null</code> if a matching layout revision could not be found
	 */
	public LayoutRevision fetchByL_P_S_First(
		long layoutSetBranchId, long plid, int status,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator);

	/**
	 * Removes all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 */
	public void removeByL_P_S(long layoutSetBranchId, long plid, int status);

	/**
	 * Returns the number of layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout revisions
	 */
	public int countByL_P_S(long layoutSetBranchId, long plid, int status);

	/**
	 * Creates a new layout revision with the primary key. Does not add the layout revision to the database.
	 *
	 * @param layoutRevisionId the primary key for the new layout revision
	 * @return the new layout revision
	 */
	public LayoutRevision create(long layoutRevisionId);

	/**
	 * Removes the layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision that was removed
	 * @throws NoSuchLayoutRevisionException if a layout revision with the primary key could not be found
	 */
	public LayoutRevision remove(long layoutRevisionId)
		throws NoSuchLayoutRevisionException;

	public LayoutRevision updateImpl(LayoutRevision layoutRevision);

	/**
	 * Returns the layout revision with the primary key or throws a <code>NoSuchLayoutRevisionException</code> if it could not be found.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision
	 * @throws NoSuchLayoutRevisionException if a layout revision with the primary key could not be found
	 */
	public LayoutRevision findByPrimaryKey(long layoutRevisionId)
		throws NoSuchLayoutRevisionException;

	/**
	 * Returns the layout revision with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision, or <code>null</code> if a layout revision with the primary key could not be found
	 */
	public LayoutRevision fetchByPrimaryKey(long layoutRevisionId);

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByLayoutSetBranchId(
		long layoutSetBranchId) {

		return findByLayoutSetBranchId(
			layoutSetBranchId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByPlid(long plid) {
		return findByPlid(
			plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByPlid(
		long plid, int start, int end) {

		return findByPlid(plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByPlid(
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByPlid(plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where status = &#63;.
	 *
	 * @param status the status
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByStatus(int status) {
		return findByStatus(
			status, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByStatus(
		int status, int start, int end) {

		return findByStatus(status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByStatus(
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByStatus(status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H(
		long layoutSetBranchId, boolean head) {

		return findByL_H(
			layoutSetBranchId, head,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H(
		long layoutSetBranchId, boolean head, int start, int end) {

		return findByL_H(layoutSetBranchId, head, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H(
		long layoutSetBranchId, boolean head, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_H(
			layoutSetBranchId, head, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P(
		long layoutSetBranchId, long plid) {

		return findByL_P(
			layoutSetBranchId, plid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P(
		long layoutSetBranchId, long plid, int start, int end) {

		return findByL_P(layoutSetBranchId, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_P(
			layoutSetBranchId, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_S(
		long layoutSetBranchId, int status) {

		return findByL_S(
			layoutSetBranchId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_S(
		long layoutSetBranchId, int status, int start, int end) {

		return findByL_S(layoutSetBranchId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_S(
		long layoutSetBranchId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_S(
			layoutSetBranchId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where head = &#63; and plid = &#63;.
	 *
	 * @param head the head
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByH_P(
		boolean head, long plid) {

		return findByH_P(
			head, plid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByH_P(
		boolean head, long plid, int start, int end) {

		return findByH_P(head, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByH_P(
		boolean head, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByH_P(head, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return findByL_L_P(
			layoutSetBranchId, layoutBranchId, plid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end) {

		return findByL_L_P(
			layoutSetBranchId, layoutBranchId, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and layoutBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param layoutBranchId the layout branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_L_P(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_L_P(
			layoutSetBranchId, layoutBranchId, plid, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		return findByL_P_P(
			layoutSetBranchId, parentLayoutRevisionId, plid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		int start, int end) {

		return findByL_P_P(
			layoutSetBranchId, parentLayoutRevisionId, plid, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and parentLayoutRevisionId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param parentLayoutRevisionId the parent layout revision ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_P(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_P_P(
			layoutSetBranchId, parentLayoutRevisionId, plid, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid) {

		return findByL_H_P_Collection(
			layoutSetBranchId, head, plid,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid, int start, int end) {

		return findByL_H_P_Collection(
			layoutSetBranchId, head, plid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param plid the plid
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_P_Collection(
		long layoutSetBranchId, boolean head, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_H_P_Collection(
			layoutSetBranchId, head, plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_S(
		long layoutSetBranchId, boolean head, int status) {

		return findByL_H_S(
			layoutSetBranchId, head, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_S(
		long layoutSetBranchId, boolean head, int status, int start, int end) {

		return findByL_H_S(
			layoutSetBranchId, head, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and head = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param head the head
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_H_S(
		long layoutSetBranchId, boolean head, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_H_S(
			layoutSetBranchId, head, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @return the matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_S(
		long layoutSetBranchId, long plid, int status) {

		return findByL_P_S(
			layoutSetBranchId, plid, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_S(
		long layoutSetBranchId, long plid, int status, int start, int end) {

		return findByL_P_S(
			layoutSetBranchId, plid, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout revisions where layoutSetBranchId = &#63; and plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout revisions
	 */
	public default java.util.List<LayoutRevision> findByL_P_S(
		long layoutSetBranchId, long plid, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutRevision>
			orderByComparator) {

		return findByL_P_S(
			layoutSetBranchId, plid, status, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-472140600