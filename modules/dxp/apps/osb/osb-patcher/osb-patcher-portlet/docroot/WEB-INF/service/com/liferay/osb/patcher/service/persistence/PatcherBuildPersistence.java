/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherBuild;

import com.liferay.portal.service.persistence.BasePersistence;

/**
 * The persistence interface for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildPersistenceImpl
 * @see PatcherBuildUtil
 * @generated
 */
public interface PatcherBuildPersistence extends BasePersistence<PatcherBuild> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PatcherBuildUtil} to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the matching patcher build
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild findByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild fetchByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param key the key
	* @param keyVersion the key version
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild fetchByK_KV(
		java.lang.String key, double keyVersion, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the patcher build that was removed
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild removeByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the number of matching patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public int countByK_KV(java.lang.String key, double keyVersion)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Caches the patcher build in the entity cache if it is enabled.
	*
	* @param patcherBuild the patcher build
	*/
	public void cacheResult(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild);

	/**
	* Caches the patcher builds in the entity cache if it is enabled.
	*
	* @param patcherBuilds the patcher builds
	*/
	public void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds);

	/**
	* Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	*
	* @param patcherBuildId the primary key for the new patcher build
	* @return the new patcher build
	*/
	public com.liferay.osb.patcher.model.PatcherBuild create(
		long patcherBuildId);

	/**
	* Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild remove(
		long patcherBuildId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException;

	public com.liferay.osb.patcher.model.PatcherBuild updateImpl(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild findByPrimaryKey(
		long patcherBuildId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public com.liferay.osb.patcher.model.PatcherBuild fetchByPrimaryKey(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher builds.
	*
	* @return the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher builds.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @return the range of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher builds.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes all the patcher builds from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher builds.
	*
	* @return the number of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public int countAll()
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher accounts associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher accounts associated with the patcher build.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher build
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @return the range of patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher accounts associated with the patcher build.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher build
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher accounts associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the number of patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherAccountsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher account is associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	*
	* @param pk the primary key of the patcher build to check for associations with patcher accounts
	* @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherAccounts(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccount the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build to clear the associated patcher accounts from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherAccounts(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccount the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns all the patcher fixs associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk) throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns a range of all the patcher fixs associated with the patcher build.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher build
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @return the range of patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns an ordered range of all the patcher fixs associated with the patcher build.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param pk the primary key of the patcher build
	* @param start the lower bound of the range of patcher builds
	* @param end the upper bound of the range of patcher builds (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns the number of patcher fixs associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the number of patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public int getPatcherFixsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher fix is associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Returns <code>true</code> if the patcher build has any patcher fixs associated with it.
	*
	* @param pk the primary key of the patcher build to check for associations with patcher fixs
	* @return <code>true</code> if the patcher build has any patcher fixs associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public boolean containsPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void addPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Clears all associations between the patcher build and its patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build to clear the associated patcher fixs from
	* @throws SystemException if a system exception occurred
	*/
	public void clearPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public void removePatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException;

	/**
	* Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public void setPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException;
}