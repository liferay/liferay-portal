/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProductVersion;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher product version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProductVersionPersistenceImpl
 * @see PatcherProductVersionUtil
 * @generated
 */
public interface PatcherProductVersionPersistence extends BasePersistence<PatcherProductVersion> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherProductVersionUtil} to access the patcher product version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Caches the patcher product version in the entity cache if it is enabled.
	*
	* @param patcherProductVersion the patcher product version
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion);

	/**
	* Caches the patcher product versions in the entity cache if it is enabled.
	*
	* @param patcherProductVersions the patcher product versions
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> patcherProductVersions);

	/**
	* Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	*
	* @param patcherProductVersionId the primary key for the new patcher product version
	* @return the new patcher product version
	*/
	public com.liferay.osb.patcher.model.PatcherProductVersion create(
		long patcherProductVersionId);

	/**
	* Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherProductVersion remove(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherProductVersion updateImpl(
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher product version with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherProductVersionException} if it could not be found.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version
	* @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherProductVersion findByPrimaryKey(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherProductVersionId the primary key of the patcher product version
	* @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherProductVersion fetchByPrimaryKey(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher product versions.
	*
	* @return the patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher product versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher product versions
	* @param end the upper bound of the range of patcher product versions (not inclusive)
	* @return the range of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher product versions.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher product versions
	* @param end the upper bound of the range of patcher product versions (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherProductVersion> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher product versions from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher product versions.
	*
	* @return the number of patcher product versions
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}