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

import com.liferay.osb.patcher.model.PatcherBuildRel;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher build rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildRelPersistenceImpl
 * @see PatcherBuildRelUtil
 * @generated
 */
public interface PatcherBuildRelPersistence extends BasePersistence<PatcherBuildRel> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherBuildRelUtil} to access the patcher build rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Caches the patcher build rel in the entity cache if it is enabled.
	*
	* @param patcherBuildRel the patcher build rel
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel);

	/**
	* Caches the patcher build rels in the entity cache if it is enabled.
	*
	* @param patcherBuildRels the patcher build rels
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> patcherBuildRels);

	/**
	* Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	*
	* @param patcherBuildRelId the primary key for the new patcher build rel
	* @return the new patcher build rel
	*/
	public com.liferay.osb.patcher.model.PatcherBuildRel create(
		long patcherBuildRelId);

	/**
	* Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuildRel remove(
		long patcherBuildRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherBuildRel updateImpl(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildRelException} if it could not be found.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuildRel findByPrimaryKey(
		long patcherBuildRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuildRel fetchByPrimaryKey(
		long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher build rels.
	*
	* @return the patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher build rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher build rels
	* @param end the upper bound of the range of patcher build rels (not inclusive)
	* @return the range of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher build rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher build rels
	* @param end the upper bound of the range of patcher build rels (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher build rels from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher build rels.
	*
	* @return the number of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}