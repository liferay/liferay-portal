/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildUtil
 * @generated
 */
@ProviderType
public interface PatcherBuildPersistence extends BasePersistence<PatcherBuild> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherBuildUtil} to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherFixId(long patcherFixId);

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByPatcherFixId_First(
			long patcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByPatcherFixId_First(
		long patcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByPatcherFixId_Last(
			long patcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByPatcherFixId_Last(
		long patcherFixId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 */
	public void removeByPatcherFixId(long patcherFixId);

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds
	 */
	public int countByPatcherFixId(long patcherFixId);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByPatcherFixId(long patcherFixId);

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId);

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByPatcherProjectVersionId_Last(
			long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByPatcherProjectVersionId_Last(
		long patcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByPatcherProjectVersionId_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByPatcherProjectVersionId_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	public void removeByPatcherProjectVersionId(long patcherProjectVersionId);

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds
	 */
	public int countByPatcherProjectVersionId(long patcherProjectVersionId);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId);

	/**
	 * Returns all the patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByKey(String key);

	/**
	 * Returns a range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByKey(
		String key, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByKey(
		String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByKey(
		String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByKey_First(
			String key,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByKey_First(
		String key,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByKey_Last(
			String key,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByKey_Last(
		String key,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByKey_PrevAndNext(
			long patcherBuildId, String key,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByKey(String key);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByKey(
		String key, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByKey(
		String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByKey_PrevAndNext(
			long patcherBuildId, String key,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	public void removeByKey(String key);

	/**
	 * Returns the number of patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds
	 */
	public int countByKey(String key);

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByKey(String key);

	/**
	 * Returns all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId);

	/**
	 * Returns a range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_P_First(
			long patcherAccountId, long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_P_First(
		long patcherAccountId, long patcherProductVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_P_Last(
			long patcherAccountId, long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_P_Last(
		long patcherAccountId, long patcherProductVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 */
	public void removeByP_P(
		long patcherAccountId, long patcherProductVersionId);

	/**
	 * Returns the number of patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds
	 */
	public int countByP_P(long patcherAccountId, long patcherProductVersionId);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByP_P(
		long patcherAccountId, long patcherProductVersionId);

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild);

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_C_First(
			long patcherFixId, boolean childBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_C_First(
		long patcherFixId, boolean childBuild,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_C_Last(
			long patcherFixId, boolean childBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_C_Last(
		long patcherFixId, boolean childBuild,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and childBuild = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 */
	public void removeByP_C(long patcherFixId, boolean childBuild);

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds
	 */
	public int countByP_C(long patcherFixId, boolean childBuild);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByP_C(long patcherFixId, boolean childBuild);

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_KV(String key, double keyVersion);

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache);

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public int countByK_KV(String key, double keyVersion);

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion);

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_GtKV_First(
			String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_GtKV_First(
		String key, double keyVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_GtKV_Last(
			String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_GtKV_Last(
		String key, double keyVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &gt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	public void removeByK_GtKV(String key, double keyVersion);

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public int countByK_GtKV(String key, double keyVersion);

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByK_GtKV(String key, double keyVersion);

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion);

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_LtKV_First(
			String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_LtKV_First(
		String key, double keyVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_LtKV_Last(
			String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_LtKV_Last(
		String key, double keyVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &lt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	public void removeByK_LtKV(String key, double keyVersion);

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public int countByK_LtKV(String key, double keyVersion);

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByK_LtKV(String key, double keyVersion);

	/**
	 * Returns all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild);

	/**
	 * Returns a range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_L_First(
			String key, boolean latestKeyBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_L_First(
		String key, boolean latestKeyBuild,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByK_L_Last(
			String key, boolean latestKeyBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByK_L_Last(
		String key, boolean latestKeyBuild,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where key = &#63; and latestKeyBuild = &#63; from the database.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 */
	public void removeByK_L(String key, boolean latestKeyBuild);

	/**
	 * Returns the number of patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds
	 */
	public int countByK_L(String key, boolean latestKeyBuild);

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByK_L(String key, boolean latestKeyBuild);

	/**
	 * Returns all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket);

	/**
	 * Returns a range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByL_S_First(
			boolean latestSupportTicketBuild, String supportTicket,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByL_S_First(
		boolean latestSupportTicketBuild, String supportTicket,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByL_S_Last(
			boolean latestSupportTicketBuild, String supportTicket,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByL_S_Last(
		boolean latestSupportTicketBuild, String supportTicket,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63; from the database.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 */
	public void removeByL_S(
		boolean latestSupportTicketBuild, String supportTicket);

	/**
	 * Returns the number of patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds
	 */
	public int countByL_S(
		boolean latestSupportTicketBuild, String supportTicket);

	/**
	 * Returns the number of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByL_S(
		boolean latestSupportTicketBuild, String supportTicket);

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByS_GtS_First(
			String supportTicket, double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByS_GtS_First(
		String supportTicket, double supportTicketVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByS_GtS_Last(
			String supportTicket, double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByS_GtS_Last(
		String supportTicket, double supportTicketVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	public void removeByS_GtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	public int countByS_GtS(String supportTicket, double supportTicketVersion);

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByS_GtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByS_LtS_First(
			String supportTicket, double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByS_LtS_First(
		String supportTicket, double supportTicketVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByS_LtS_Last(
			String supportTicket, double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByS_LtS_Last(
		String supportTicket, double supportTicketVersion,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	public void removeByS_LtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	public int countByS_LtS(String supportTicket, double supportTicketVersion);

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByS_LtS(
		String supportTicket, double supportTicketVersion);

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status);

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByLtM_N_S_First(
			Date modifiedDate, boolean notified, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByLtM_N_S_First(
		Date modifiedDate, boolean notified, int status,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByLtM_N_S_Last(
			Date modifiedDate, boolean notified, int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByLtM_N_S_Last(
		Date modifiedDate, boolean notified, int status,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses);

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 */
	public void removeByLtM_N_S(
		Date modifiedDate, boolean notified, int status);

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds
	 */
	public int countByLtM_N_S(Date modifiedDate, boolean notified, int status);

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds
	 */
	public int countByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses);

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int status);

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses);

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type);

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_NotP_C_NotT_First(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_NotP_C_NotT_First(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_NotP_C_NotT_Last(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_NotP_C_NotT_Last(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 */
	public void removeByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type);

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds
	 */
	public int countByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type);

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name);

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public java.util.List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_N_L_A_First(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_N_L_A_First(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public PatcherBuild findByP_N_L_A_Last(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public PatcherBuild fetchByP_N_L_A_Last(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] findByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name);

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public java.util.List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild[] filterFindByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
				orderByComparator)
		throws NoSuchPatcherBuildException;

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 */
	public void removeByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name);

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds
	 */
	public int countByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name);

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public int filterCountByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name);

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	public void cacheResult(PatcherBuild patcherBuild);

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	public void cacheResult(java.util.List<PatcherBuild> patcherBuilds);

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	public PatcherBuild create(long patcherBuildId);

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException;

	public PatcherBuild updateImpl(PatcherBuild patcherBuild);

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException;

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId);

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 */
	public java.util.List<PatcherBuild> findAll();

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher builds
	 */
	public java.util.List<PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher builds from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	public int countAll();

	/**
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	public long[] getPatcherAccountPrimaryKeys(long pk);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end);

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	public java.util.List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	public int getPatcherAccountsSize(long pk);

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	public boolean containsPatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	public boolean containsPatcherAccounts(long pk);

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount);

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	public void clearPatcherAccounts(long pk);

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	public void removePatcherAccount(long pk, long patcherAccountPK);

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	public void removePatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount);

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	public void removePatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs);

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	public void setPatcherAccounts(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount>
			patcherAccounts);

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	public long[] getPatcherFixPrimaryKeys(long pk);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(long pk);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end);

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	public java.util.List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherBuild>
			orderByComparator);

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	public int getPatcherFixesSize(long pk);

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	public boolean containsPatcherFix(long pk, long patcherFixPK);

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	public boolean containsPatcherFixes(long pk);

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(long pk, long patcherFixPK);

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public boolean addPatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public boolean addPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	public void clearPatcherFixes(long pk);

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	public void removePatcherFix(long pk, long patcherFixPK);

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	public void removePatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	public void removePatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	public void removePatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	public void setPatcherFixes(long pk, long[] patcherFixPKs);

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	public void setPatcherFixes(
		long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes);

}