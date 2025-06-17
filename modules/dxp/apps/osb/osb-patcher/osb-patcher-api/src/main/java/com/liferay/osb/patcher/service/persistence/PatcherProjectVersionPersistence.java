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