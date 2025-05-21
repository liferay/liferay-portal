/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the patcher fix component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponentUtil
 * @generated
 */
@ProviderType
public interface PatcherFixComponentPersistence
	extends BasePersistence<PatcherFixComponent> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixComponentUtil} to access the patcher fix component persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Caches the patcher fix component in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponent the patcher fix component
	 */
	public void cacheResult(PatcherFixComponent patcherFixComponent);

	/**
	 * Caches the patcher fix components in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponents the patcher fix components
	 */
	public void cacheResult(
		java.util.List<PatcherFixComponent> patcherFixComponents);

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	public PatcherFixComponent create(long patcherFixComponentId);

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	public PatcherFixComponent remove(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException;

	public PatcherFixComponent updateImpl(
		PatcherFixComponent patcherFixComponent);

	/**
	 * Returns the patcher fix component with the primary key or throws a <code>NoSuchPatcherFixComponentException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	public PatcherFixComponent findByPrimaryKey(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException;

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 */
	public PatcherFixComponent fetchByPrimaryKey(long patcherFixComponentId);

	/**
	 * Returns all the patcher fix components.
	 *
	 * @return the patcher fix components
	 */
	public java.util.List<PatcherFixComponent> findAll();

	/**
	 * Returns a range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @return the range of patcher fix components
	 */
	public java.util.List<PatcherFixComponent> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix components
	 */
	public java.util.List<PatcherFixComponent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixComponent>
			orderByComparator);

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix components
	 */
	public java.util.List<PatcherFixComponent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<PatcherFixComponent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the patcher fix components from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of patcher fix components.
	 *
	 * @return the number of patcher fix components
	 */
	public int countAll();

}