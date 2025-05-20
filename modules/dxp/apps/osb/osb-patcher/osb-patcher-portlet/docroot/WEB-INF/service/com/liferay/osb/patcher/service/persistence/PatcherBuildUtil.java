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

import com.liferay.osb.patcher.model.PatcherBuild;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher build service. This utility wraps {@link PatcherBuildPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildPersistence
 * @see PatcherBuildPersistenceImpl
 * @generated
 */
public class PatcherBuildUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#clearCache(com.liferay.portal.model.BaseModel)
	 */
	public static void clearCache(PatcherBuild patcherBuild) {
		getPersistence().clearCache(patcherBuild);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery)
		throws SystemException {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherBuild update(PatcherBuild patcherBuild)
		throws SystemException {
		return getPersistence().update(patcherBuild);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherBuild update(PatcherBuild patcherBuild,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherBuild, serviceContext);
	}

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the matching patcher build
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild findByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByK_KV(key, keyVersion);
	}

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild fetchByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByK_KV(key, keyVersion);
	}

	/**
	* Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param key the key
	* @param keyVersion the key version
	* @param retrieveFromCache whether to use the finder cache
	* @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild fetchByK_KV(
		java.lang.String key, double keyVersion, boolean retrieveFromCache)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByK_KV(key, keyVersion, retrieveFromCache);
	}

	/**
	* Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the patcher build that was removed
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild removeByK_KV(
		java.lang.String key, double keyVersion)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().removeByK_KV(key, keyVersion);
	}

	/**
	* Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	*
	* @param key the key
	* @param keyVersion the key version
	* @return the number of matching patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static int countByK_KV(java.lang.String key, double keyVersion)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countByK_KV(key, keyVersion);
	}

	/**
	* Caches the patcher build in the entity cache if it is enabled.
	*
	* @param patcherBuild the patcher build
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {
		getPersistence().cacheResult(patcherBuild);
	}

	/**
	* Caches the patcher builds in the entity cache if it is enabled.
	*
	* @param patcherBuilds the patcher builds
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {
		getPersistence().cacheResult(patcherBuilds);
	}

	/**
	* Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	*
	* @param patcherBuildId the primary key for the new patcher build
	* @return the new patcher build
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild create(
		long patcherBuildId) {
		return getPersistence().create(patcherBuildId);
	}

	/**
	* Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild remove(
		long patcherBuildId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherBuildId);
	}

	public static com.liferay.osb.patcher.model.PatcherBuild updateImpl(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherBuild);
	}

	/**
	* Returns the patcher build with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild findByPrimaryKey(
		long patcherBuildId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherBuildId);
	}

	/**
	* Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherBuildId the primary key of the patcher build
	* @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuild fetchByPrimaryKey(
		long patcherBuildId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherBuildId);
	}

	/**
	* Returns all the patcher builds.
	*
	* @return the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher builds from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher builds.
	*
	* @return the number of patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	/**
	* Returns all the patcher accounts associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk) throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherAccounts(pk);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherAccounts(pk, start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .getPatcherAccounts(pk, start, end, orderByComparator);
	}

	/**
	* Returns the number of patcher accounts associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the number of patcher accounts associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherAccountsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherAccountsSize(pk);
	}

	/**
	* Returns <code>true</code> if the patcher account is associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherAccount(pk, patcherAccountPK);
	}

	/**
	* Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	*
	* @param pk the primary key of the patcher build to check for associations with patcher accounts
	* @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherAccounts(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherAccounts(pk);
	}

	/**
	* Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherAccount(pk, patcherAccountPK);
	}

	/**
	* Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccount the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherAccount(pk, patcherAccount);
	}

	/**
	* Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	* Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherAccounts(pk, patcherAccounts);
	}

	/**
	* Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build to clear the associated patcher accounts from
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherAccounts(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().clearPatcherAccounts(pk);
	}

	/**
	* Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPK the primary key of the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherAccount(long pk, long patcherAccountPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherAccount(pk, patcherAccountPK);
	}

	/**
	* Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccount the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherAccount(pk, patcherAccount);
	}

	/**
	* Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	* Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherAccounts(pk, patcherAccounts);
	}

	/**
	* Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	* Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherAccounts the patcher accounts to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherAccounts(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherAccounts(pk, patcherAccounts);
	}

	/**
	* Returns all the patcher fixs associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk) throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixs(pk);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixs(pk, start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixs(pk, start, end, orderByComparator);
	}

	/**
	* Returns the number of patcher fixs associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @return the number of patcher fixs associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixsSize(pk);
	}

	/**
	* Returns <code>true</code> if the patcher fix is associated with the patcher build.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherFix(pk, patcherFixPK);
	}

	/**
	* Returns <code>true</code> if the patcher build has any patcher fixs associated with it.
	*
	* @param pk the primary key of the patcher build to check for associations with patcher fixs
	* @return <code>true</code> if the patcher build has any patcher fixs associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherFixs(pk);
	}

	/**
	* Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFix(pk, patcherFixPK);
	}

	/**
	* Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFix(pk, patcherFix);
	}

	/**
	* Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixs(pk, patcherFixPKs);
	}

	/**
	* Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixs(pk, patcherFixs);
	}

	/**
	* Clears all associations between the patcher build and its patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build to clear the associated patcher fixs from
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherFixs(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().clearPatcherFixs(pk);
	}

	/**
	* Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPK the primary key of the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFix(long pk, long patcherFixPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFix(pk, patcherFixPK);
	}

	/**
	* Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFix the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFix(pk, patcherFix);
	}

	/**
	* Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixs(pk, patcherFixPKs);
	}

	/**
	* Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixs(pk, patcherFixs);
	}

	/**
	* Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixPKs the primary keys of the patcher fixs to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixs(long pk, long[] patcherFixPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherFixs(pk, patcherFixPKs);
	}

	/**
	* Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher build
	* @param patcherFixs the patcher fixs to be associated with the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixs(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherFixs(pk, patcherFixs);
	}

	public static PatcherBuildPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherBuildPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherBuildPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherBuildUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherBuildPersistence persistence) {
	}

	private static PatcherBuildPersistence _persistence;
}