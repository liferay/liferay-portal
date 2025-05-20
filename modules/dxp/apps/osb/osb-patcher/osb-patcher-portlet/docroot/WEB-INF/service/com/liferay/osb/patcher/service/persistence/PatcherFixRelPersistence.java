/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixRel;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher fix rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixRelPersistenceImpl
 * @see PatcherFixRelUtil
 * @generated
 */
public interface PatcherFixRelPersistence extends BasePersistence<PatcherFixRel> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixRelUtil} to access the patcher fix rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Caches the patcher fix rel in the entity cache if it is enabled.
	*
	* @param patcherFixRel the patcher fix rel
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel);

	/**
	* Caches the patcher fix rels in the entity cache if it is enabled.
	*
	* @param patcherFixRels the patcher fix rels
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> patcherFixRels);

	/**
	* Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	*
	* @param patcherFixRelId the primary key for the new patcher fix rel
	* @return the new patcher fix rel
	*/
	public com.liferay.osb.patcher.model.PatcherFixRel create(
		long patcherFixRelId);

	/**
	* Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixRel remove(
		long patcherFixRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixRelException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherFixRel updateImpl(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixRelException} if it could not be found.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixRel findByPrimaryKey(
		long patcherFixRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixRelException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixRelId the primary key of the patcher fix rel
	* @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixRel fetchByPrimaryKey(
		long patcherFixRelId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fix rels.
	*
	* @return the patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fix rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix rels
	* @param end the upper bound of the range of patcher fix rels (not inclusive)
	* @return the range of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fix rels.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix rels
	* @param end the upper bound of the range of patcher fix rels (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher fix rels from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fix rels.
	*
	* @return the number of patcher fix rels
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;
}