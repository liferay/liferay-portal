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

import com.liferay.osb.patcher.model.PatcherAccount;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher account service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherAccountPersistenceImpl
 * @see PatcherAccountUtil
 * @generated
 */
public interface PatcherAccountPersistence extends BasePersistence<PatcherAccount> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherAccountUtil} to access the patcher account persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Caches the patcher account in the entity cache if it is enabled.
	*
	* @param patcherAccount the patcher account
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount);

	/**
	* Caches the patcher accounts in the entity cache if it is enabled.
	*
	* @param patcherAccounts the patcher accounts
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts);

	/**
	* Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	*
	* @param patcherAccountId the primary key for the new patcher account
	* @return the new patcher account
	*/
	public com.liferay.osb.patcher.model.PatcherAccount create(
		long patcherAccountId);

	/**
	* Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherAccount remove(
		long patcherAccountId)
		throws com.liferay.osb.patcher.NoSuchPatcherAccountException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherAccount updateImpl(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher account with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherAccountException} if it could not be found.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account
	* @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherAccount findByPrimaryKey(
		long patcherAccountId)
		throws com.liferay.osb.patcher.NoSuchPatcherAccountException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherAccount fetchByPrimaryKey(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher accounts.
	*
	* @return the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher accounts.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher accounts
	* @param end the upper bound of the range of patcher accounts (not inclusive)
	* @return the range of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher accounts.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher accounts
	* @param end the upper bound of the range of patcher accounts (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher accounts from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher accounts.
	*
	* @return the number of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher builds associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @return the patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher builds associated with the patcher account.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher account
	* @param start the lower bound of the range of patcher accounts
	* @param end the upper bound of the range of patcher accounts (not inclusive)
	* @return the range of patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher builds associated with the patcher account.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher account
	* @param start the lower bound of the range of patcher accounts
	* @param end the upper bound of the range of patcher accounts (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher builds associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @return the number of patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherBuildsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher build is associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	*
	* @param pk the primary key of the patcher account to check for associations with patcher builds
	* @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account to clear the associated patcher builds from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds to be associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException;
}