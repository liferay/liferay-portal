/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher fix rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixRelUtil
 * @generated
 */
@ProviderType
public interface PatcherFixRelPersistence
	extends BasePersistence<PatcherFixRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixRelUtil} to access the patcher fix rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId);

	/**
	 * Returns a range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel findByChildPatcherFixId_First(
			long childPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel fetchByChildPatcherFixId_First(
		long childPatcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel findByChildPatcherFixId_Last(
			long childPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel fetchByChildPatcherFixId_Last(
		long childPatcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public PatcherFixRel[] findByChildPatcherFixId_PrevAndNext(
			long patcherFixRelId, long childPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Removes all the patcher fix rels where childPatcherFixId = &#63; from the database.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 */
	public void removeByChildPatcherFixId(long childPatcherFixId);

	/**
	 * Returns the number of patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	public int countByChildPatcherFixId(long childPatcherFixId);

	/**
	 * Returns all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId);

	/**
	 * Returns a range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel findByParentPatcherFixId_First(
			long parentPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel fetchByParentPatcherFixId_First(
		long parentPatcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel findByParentPatcherFixId_Last(
			long parentPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public PatcherFixRel fetchByParentPatcherFixId_Last(
		long parentPatcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public PatcherFixRel[] findByParentPatcherFixId_PrevAndNext(
			long patcherFixRelId, long parentPatcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
				orderByComparator)
		throws NoSuchPatcherFixRelException;

	/**
	 * Removes all the patcher fix rels where parentPatcherFixId = &#63; from the database.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 */
	public void removeByParentPatcherFixId(long parentPatcherFixId);

	/**
	 * Returns the number of patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	public int countByParentPatcherFixId(long parentPatcherFixId);

	/**
	 * Caches the patcher fix rel in the entity cache if it is enabled.
	 *
	 * @param patcherFixRel the patcher fix rel
	 */
	public void cacheResult(PatcherFixRel patcherFixRel);

	/**
	 * Caches the patcher fix rels in the entity cache if it is enabled.
	 *
	 * @param patcherFixRels the patcher fix rels
	 */
	public void cacheResult(java.util.List<PatcherFixRel> patcherFixRels);

	/**
	 * Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	 *
	 * @param patcherFixRelId the primary key for the new patcher fix rel
	 * @return the new patcher fix rel
	 */
	public PatcherFixRel create(long patcherFixRelId);

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public PatcherFixRel remove(long patcherFixRelId)
		throws NoSuchPatcherFixRelException;

	public PatcherFixRel updateImpl(PatcherFixRel patcherFixRel);

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>NoSuchPatcherFixRelException</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public PatcherFixRel findByPrimaryKey(long patcherFixRelId)
		throws NoSuchPatcherFixRelException;

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 */
	public PatcherFixRel fetchByPrimaryKey(long patcherFixRelId);

	/**
	 * Returns all the patcher fix rels.
	 *
	 * @return the patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findAll();

	/**
	 * Returns a range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix rels
	 */
	public java.util.List<PatcherFixRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher fix rels from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher fix rels.
	 *
	 * @return the number of patcher fix rels
	 */
	public int countAll();

}