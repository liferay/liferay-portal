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

import com.liferay.osb.patcher.model.PatcherTicketHint;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher ticket hint service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherTicketHintPersistenceImpl
 * @see PatcherTicketHintUtil
 * @generated
 */
public interface PatcherTicketHintPersistence extends BasePersistence<PatcherTicketHint> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherTicketHintUtil} to access the patcher ticket hint persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the matching patcher ticket hint
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint findByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the patcher ticket hint where patcherProductVersionId = &#63; from the database.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the patcher ticket hint that was removed
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint removeByPatcherProductVersionId(
		long patcherProductVersionId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	*
	* @param patcherProductVersionId the patcher product version ID
	* @return the number of matching patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public int countByPatcherProductVersionId(long patcherProductVersionId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the patcher ticket hint in the entity cache if it is enabled.
	*
	* @param patcherTicketHint the patcher ticket hint
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint);

	/**
	* Caches the patcher ticket hints in the entity cache if it is enabled.
	*
	* @param patcherTicketHints the patcher ticket hints
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> patcherTicketHints);

	/**
	* Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	*
	* @param patcherTicketHintId the primary key for the new patcher ticket hint
	* @return the new patcher ticket hint
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint create(
		long patcherTicketHintId);

	/**
	* Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint remove(
		long patcherTicketHintId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherTicketHint updateImpl(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher ticket hint with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint
	* @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint findByPrimaryKey(
		long patcherTicketHintId)
		throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherTicketHintId the primary key of the patcher ticket hint
	* @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherTicketHint fetchByPrimaryKey(
		long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher ticket hints.
	*
	* @return the patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher ticket hints.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher ticket hints
	* @param end the upper bound of the range of patcher ticket hints (not inclusive)
	* @return the range of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher ticket hints.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher ticket hints
	* @param end the upper bound of the range of patcher ticket hints (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher ticket hints from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher ticket hints.
	*
	* @return the number of patcher ticket hints
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}