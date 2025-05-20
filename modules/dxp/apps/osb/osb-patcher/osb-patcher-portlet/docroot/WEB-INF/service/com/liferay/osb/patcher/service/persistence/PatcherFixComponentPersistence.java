/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixComponent;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher fix component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixComponentPersistenceImpl
 * @see PatcherFixComponentUtil
 * @generated
 */
public interface PatcherFixComponentPersistence extends BasePersistence<PatcherFixComponent> {
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
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent);

	/**
	* Caches the patcher fix components in the entity cache if it is enabled.
	*
	* @param patcherFixComponents the patcher fix components
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> patcherFixComponents);

	/**
	* Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	*
	* @param patcherFixComponentId the primary key for the new patcher fix component
	* @return the new patcher fix component
	*/
	public com.liferay.osb.patcher.model.PatcherFixComponent create(
		long patcherFixComponentId);

	/**
	* Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixComponent remove(
		long patcherFixComponentId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherFixComponent updateImpl(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix component with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixComponentException} if it could not be found.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixComponent findByPrimaryKey(
		long patcherFixComponentId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixComponent fetchByPrimaryKey(
		long patcherFixComponentId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fix components.
	*
	* @return the patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fix components.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix components
	* @param end the upper bound of the range of patcher fix components (not inclusive)
	* @return the range of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fix components.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix components
	* @param end the upper bound of the range of patcher fix components (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher fix components from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fix components.
	*
	* @return the number of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}