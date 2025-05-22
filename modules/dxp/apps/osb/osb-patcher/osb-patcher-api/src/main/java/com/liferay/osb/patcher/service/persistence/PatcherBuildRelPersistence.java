/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher build rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRelUtil
 * @generated
 */
@ProviderType
public interface PatcherBuildRelPersistence
	extends BasePersistence<PatcherBuildRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherBuildRelUtil} to access the patcher build rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @return the matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByChildPatcherBuildId(
		long childPatcherBuildId);

	/**
	 * Returns a range of all the patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByChildPatcherBuildId(
		long childPatcherBuildId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByChildPatcherBuildId(
		long childPatcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByChildPatcherBuildId(
		long childPatcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel findByChildPatcherBuildId_First(
			long childPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Returns the first patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel fetchByChildPatcherBuildId_First(
		long childPatcherBuildId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns the last patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel findByChildPatcherBuildId_Last(
			long childPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Returns the last patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel fetchByChildPatcherBuildId_Last(
		long childPatcherBuildId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns the patcher build rels before and after the current patcher build rel in the ordered set where childPatcherBuildId = &#63;.
	 *
	 * @param patcherBuildRelId the primary key of the current patcher build rel
	 * @param childPatcherBuildId the child patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public PatcherBuildRel[] findByChildPatcherBuildId_PrevAndNext(
			long patcherBuildRelId, long childPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Removes all the patcher build rels where childPatcherBuildId = &#63; from the database.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 */
	public void removeByChildPatcherBuildId(long childPatcherBuildId);

	/**
	 * Returns the number of patcher build rels where childPatcherBuildId = &#63;.
	 *
	 * @param childPatcherBuildId the child patcher build ID
	 * @return the number of matching patcher build rels
	 */
	public int countByChildPatcherBuildId(long childPatcherBuildId);

	/**
	 * Returns all the patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @return the matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByParentPatcherBuildId(
		long parentPatcherBuildId);

	/**
	 * Returns a range of all the patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByParentPatcherBuildId(
		long parentPatcherBuildId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByParentPatcherBuildId(
		long parentPatcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findByParentPatcherBuildId(
		long parentPatcherBuildId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel findByParentPatcherBuildId_First(
			long parentPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Returns the first patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel fetchByParentPatcherBuildId_First(
		long parentPatcherBuildId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns the last patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel findByParentPatcherBuildId_Last(
			long parentPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Returns the last patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build rel, or <code>null</code> if a matching patcher build rel could not be found
	 */
	public PatcherBuildRel fetchByParentPatcherBuildId_Last(
		long parentPatcherBuildId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns the patcher build rels before and after the current patcher build rel in the ordered set where parentPatcherBuildId = &#63;.
	 *
	 * @param patcherBuildRelId the primary key of the current patcher build rel
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public PatcherBuildRel[] findByParentPatcherBuildId_PrevAndNext(
			long patcherBuildRelId, long parentPatcherBuildId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
				orderByComparator)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Removes all the patcher build rels where parentPatcherBuildId = &#63; from the database.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 */
	public void removeByParentPatcherBuildId(long parentPatcherBuildId);

	/**
	 * Returns the number of patcher build rels where parentPatcherBuildId = &#63;.
	 *
	 * @param parentPatcherBuildId the parent patcher build ID
	 * @return the number of matching patcher build rels
	 */
	public int countByParentPatcherBuildId(long parentPatcherBuildId);

	/**
	 * Caches the patcher build rel in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRel the patcher build rel
	 */
	public void cacheResult(PatcherBuildRel patcherBuildRel);

	/**
	 * Caches the patcher build rels in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRels the patcher build rels
	 */
	public void cacheResult(java.util.List<PatcherBuildRel> patcherBuildRels);

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	public PatcherBuildRel create(long patcherBuildRelId);

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public PatcherBuildRel remove(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException;

	public PatcherBuildRel updateImpl(PatcherBuildRel patcherBuildRel);

	/**
	 * Returns the patcher build rel with the primary key or throws a <code>NoSuchPatcherBuildRelException</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public PatcherBuildRel findByPrimaryKey(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException;

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 */
	public PatcherBuildRel fetchByPrimaryKey(long patcherBuildRelId);

	/**
	 * Returns all the patcher build rels.
	 *
	 * @return the patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findAll();

	/**
	 * Returns a range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher build rels
	 */
	public java.util.List<PatcherBuildRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuildRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher build rels from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher build rels.
	 *
	 * @return the number of patcher build rels
	 */
	public int countAll();

}