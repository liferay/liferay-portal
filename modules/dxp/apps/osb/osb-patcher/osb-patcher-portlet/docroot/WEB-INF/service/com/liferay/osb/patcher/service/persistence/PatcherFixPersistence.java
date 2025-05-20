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

import com.liferay.osb.patcher.model.PatcherFix;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher fix service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPersistenceImpl
 * @see PatcherFixUtil
 * @generated
 */
public interface PatcherFixPersistence extends BasePersistence<PatcherFix> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherFixUtil} to access the patcher fix persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Caches the patcher fix in the entity cache if it is enabled.
	*
	* @param patcherFix the patcher fix
	*/
	public void cacheResult(com.liferay.osb.patcher.model.PatcherFix patcherFix);

	/**
	* Caches the patcher fixs in the entity cache if it is enabled.
	*
	* @param patcherFixs the patcher fixs
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs);

	/**
	* Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	*
	* @param patcherFixId the primary key for the new patcher fix
	* @return the new patcher fix
	*/
	public com.liferay.osb.patcher.model.PatcherFix create(long patcherFixId);

	/**
	* Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFix remove(long patcherFixId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherFix updateImpl(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixException} if it could not be found.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFix findByPrimaryKey(
		long patcherFixId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherFix fetchByPrimaryKey(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fixs.
	*
	* @return the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fixs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @return the range of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fixs.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher fixs from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fixs.
	*
	* @return the number of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher builds associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher builds associated with the patcher fix.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @return the range of patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher builds associated with the patcher fix.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher builds associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the number of patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherBuildsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher build is associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	*
	* @param pk the primary key of the patcher fix to check for associations with patcher builds
	* @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix to clear the associated patcher builds from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fix packs associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fix packs associated with the patcher fix.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @return the range of patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fix packs associated with the patcher fix.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher fix
	* @param start the lower bound of the range of patcher fixs
	* @param end the upper bound of the range of patcher fixs (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fix packs associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the number of patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherFixPacksSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	*
	* @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	* @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFixPacks(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPack the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherFixPacks(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPack the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException;
}