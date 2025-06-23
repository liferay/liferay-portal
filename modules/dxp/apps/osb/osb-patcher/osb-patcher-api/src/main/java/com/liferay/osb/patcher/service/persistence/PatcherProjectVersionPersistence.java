/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher project version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersionUtil
 * @generated
 */
@ProviderType
public interface PatcherProjectVersionPersistence
	extends BasePersistence<PatcherProjectVersion> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherProjectVersionUtil} to access the patcher project version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId);

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByPatcherProductVersionId_First(
			long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByPatcherProductVersionId_First(
		long patcherProductVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByPatcherProductVersionId_Last(
			long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByPatcherProductVersionId_Last(
		long patcherProductVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[] findByPatcherProductVersionId_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(long patcherProductVersionId);

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(
			long patcherProductVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(
			long patcherProductVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[]
			filterFindByPatcherProductVersionId_PrevAndNext(
				long patcherProjectVersionId, long patcherProductVersionId,
				com.liferay.portal.kernel.util.OrderByComparator
					<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 */
	public void removeByPatcherProductVersionId(long patcherProductVersionId);

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions
	 */
	public int countByPatcherProductVersionId(long patcherProductVersionId);

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public int filterCountByPatcherProductVersionId(
		long patcherProductVersionId);

	/**
	 * Returns all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion>
		findByRootPatcherProjectVersionId(long rootPatcherProjectVersionId);

	/**
	 * Returns a range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion>
		findByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion>
		findByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator);

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion>
		findByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByRootPatcherProjectVersionId_First(
			long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByRootPatcherProjectVersionId_First(
		long rootPatcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByRootPatcherProjectVersionId_Last(
			long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByRootPatcherProjectVersionId_Last(
		long rootPatcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[]
			findByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				com.liferay.portal.kernel.util.OrderByComparator
					<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId);

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[]
			filterFindByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				com.liferay.portal.kernel.util.OrderByComparator
					<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Removes all the patcher project versions where rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	public void removeByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId);

	/**
	 * Returns the number of patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	public int countByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId);

	/**
	 * Returns the number of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public int filterCountByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId);

	/**
	 * Returns the patcher project version where committish = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByCommittish(String committish);

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param committish the committish
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByCommittish(
		String committish, boolean useFinderCache);

	/**
	 * Removes the patcher project version where committish = &#63; from the database.
	 *
	 * @param committish the committish
	 * @return the patcher project version that was removed
	 */
	public PatcherProjectVersion removeByCommittish(String committish)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the number of patcher project versions where committish = &#63;.
	 *
	 * @param committish the committish
	 * @return the number of matching patcher project versions
	 */
	public int countByCommittish(String committish);

	/**
	 * Returns the patcher project version where name = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByName(String name)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByName(String name);

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByName(
		String name, boolean useFinderCache);

	/**
	 * Removes the patcher project version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher project version that was removed
	 */
	public PatcherProjectVersion removeByName(String name)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the number of patcher project versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher project versions
	 */
	public int countByName(String name);

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId);

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByP_R_First(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByP_R_First(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByP_R_Last(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByP_R_Last(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[] findByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId);

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[] filterFindByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	public void removeByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId);

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	public int countByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId);

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public int filterCountByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId);

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName);

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByP_RN_First(
			long patcherProductVersionId, String repositoryName,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByP_RN_First(
		long patcherProductVersionId, String repositoryName,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion findByP_RN_Last(
			long patcherProductVersionId, String repositoryName,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public PatcherProjectVersion fetchByP_RN_Last(
		long patcherProductVersionId, String repositoryName,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[] findByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName);

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end);

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public java.util.List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion[] filterFindByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			com.liferay.portal.kernel.util.OrderByComparator
				<PatcherProjectVersion> orderByComparator)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 */
	public void removeByP_RN(
		long patcherProductVersionId, String repositoryName);

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions
	 */
	public int countByP_RN(long patcherProductVersionId, String repositoryName);

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public int filterCountByP_RN(
		long patcherProductVersionId, String repositoryName);

	/**
	 * Caches the patcher project version in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersion the patcher project version
	 */
	public void cacheResult(PatcherProjectVersion patcherProjectVersion);

	/**
	 * Caches the patcher project versions in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersions the patcher project versions
	 */
	public void cacheResult(
		java.util.List<PatcherProjectVersion> patcherProjectVersions);

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	public PatcherProjectVersion create(long patcherProjectVersionId);

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion remove(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException;

	public PatcherProjectVersion updateImpl(
		PatcherProjectVersion patcherProjectVersion);

	/**
	 * Returns the patcher project version with the primary key or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion findByPrimaryKey(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException;

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 */
	public PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId);

	/**
	 * Returns all the patcher project versions.
	 *
	 * @return the patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findAll();

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher project versions
	 */
	public java.util.List<PatcherProjectVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherProjectVersion>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher project versions from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
	 */
	public int countAll();

}