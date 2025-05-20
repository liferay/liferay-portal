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

import com.liferay.osb.patcher.model.PatcherFixPack;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPackPersistenceImpl
 * @see PatcherFixPackUtil
 * @generated
 */
public interface PatcherFixPackPersistence extends BasePersistence<PatcherFixPack> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixPackUtil} to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixPackException} if it could not be found.
	*
	* @param patcherFixComponentId the patcher fix component ID
	* @param patcherProjectVersionId the patcher project version ID
	* @param name the name
	* @param version the version
	* @return the matching patcher fix pack
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack findByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId,
		java.lang.String name, int version)
		throws com.liferay.osb.patcher.NoSuchPatcherFixPackException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param patcherFixComponentId the patcher fix component ID
	* @param patcherProjectVersionId the patcher project version ID
	* @param name the name
	* @param version the version
	* @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId,
		java.lang.String name, int version)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param patcherFixComponentId the patcher fix component ID
	* @param patcherProjectVersionId the patcher project version ID
	* @param name the name
	* @param version the version
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId,
		java.lang.String name, int version, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	*
	* @param patcherFixComponentId the patcher fix component ID
	* @param patcherProjectVersionId the patcher project version ID
	* @param name the name
	* @param version the version
	* @return the patcher fix pack that was removed
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack removeByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId,
		java.lang.String name, int version)
		throws com.liferay.osb.patcher.NoSuchPatcherFixPackException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	*
	* @param patcherFixComponentId the patcher fix component ID
	* @param patcherProjectVersionId the patcher project version ID
	* @param name the name
	* @param version the version
	* @return the number of matching patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public int countByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, java.lang.String name, int version)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the patcher fix pack in the entity cache if it is enabled.
	*
	* @param patcherFixPack the patcher fix pack
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack);

	/**
	* Caches the patcher fix packs in the entity cache if it is enabled.
	*
	* @param patcherFixPacks the patcher fix packs
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks);

	/**
	* Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	*
	* @param patcherFixPackId the primary key for the new patcher fix pack
	* @return the new patcher fix pack
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack create(
		long patcherFixPackId);

	/**
	* Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack remove(
		long patcherFixPackId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixPackException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherFixPack updateImpl(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix pack with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixPackException} if it could not be found.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack findByPrimaryKey(
		long patcherFixPackId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixPackException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixPackId the primary key of the patcher fix pack
	* @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFixPack fetchByPrimaryKey(
		long patcherFixPackId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fix packs.
	*
	* @return the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fix packs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix packs
	* @param end the upper bound of the range of patcher fix packs (not inclusive)
	* @return the range of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fix packs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix packs
	* @param end the upper bound of the range of patcher fix packs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher fix packs from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fix packs.
	*
	* @return the number of patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fixs associated with the patcher fix pack.
	*
	* @param pk the primary key of the patcher fix pack
	* @return the patcher fixs associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fixs associated with the patcher fix pack.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix pack
	* @param start the lower bound of the range of patcher fix packs
	* @param end the upper bound of the range of patcher fix packs (not inclusive)
	* @return the range of patcher fixs associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fixs associated with the patcher fix pack.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix pack
	* @param start the lower bound of the range of patcher fix packs
	* @param end the upper bound of the range of patcher fix packs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fixs associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fixs associated with the patcher fix pack.
	*
	* @param pk the primary key of the patcher fix pack
	* @return the number of patcher fixs associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherFixsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPK the primary key of the patcher fix
	* @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix pack has any patcher fixs associated with it.
	*
	* @param pk the primary key of the patcher fix pack to check for associations with patcher fixs
	* @return <code>true</code> if the patcher fix pack has any patcher fixs associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher fix pack and its patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack to clear the associated patcher fixs from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fixs associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixPKs the primary keys of the patcher fixs to be associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fixs associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix pack
	* @param patcherFixs the patcher fixs to be associated with the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;
}