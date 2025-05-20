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

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher account service. This utility wraps {@link PatcherAccountPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherAccountPersistence
 * @see PatcherAccountPersistenceImpl
 * @generated
 */
public class PatcherAccountUtil {
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
	public static void clearCache(PatcherAccount patcherAccount) {
		getPersistence().clearCache(patcherAccount);
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
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherAccount update(PatcherAccount patcherAccount)
		throws SystemException {
		return getPersistence().update(patcherAccount);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherAccount update(PatcherAccount patcherAccount,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherAccount, serviceContext);
	}

	/**
	* Caches the patcher account in the entity cache if it is enabled.
	*
	* @param patcherAccount the patcher account
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {
		getPersistence().cacheResult(patcherAccount);
	}

	/**
	* Caches the patcher accounts in the entity cache if it is enabled.
	*
	* @param patcherAccounts the patcher accounts
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts) {
		getPersistence().cacheResult(patcherAccounts);
	}

	/**
	* Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	*
	* @param patcherAccountId the primary key for the new patcher account
	* @return the new patcher account
	*/
	public static com.liferay.osb.patcher.model.PatcherAccount create(
		long patcherAccountId) {
		return getPersistence().create(patcherAccountId);
	}

	/**
	* Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherAccount remove(
		long patcherAccountId)
		throws com.liferay.osb.patcher.NoSuchPatcherAccountException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherAccountId);
	}

	public static com.liferay.osb.patcher.model.PatcherAccount updateImpl(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherAccount);
	}

	/**
	* Returns the patcher account with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherAccountException} if it could not be found.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account
	* @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherAccount findByPrimaryKey(
		long patcherAccountId)
		throws com.liferay.osb.patcher.NoSuchPatcherAccountException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherAccountId);
	}

	/**
	* Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherAccountId the primary key of the patcher account
	* @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherAccount fetchByPrimaryKey(
		long patcherAccountId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherAccountId);
	}

	/**
	* Returns all the patcher accounts.
	*
	* @return the patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherAccount> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher accounts from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher accounts.
	*
	* @return the number of patcher accounts
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	/**
	* Returns all the patcher builds associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @return the patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuilds(pk);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuilds(pk, start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .getPatcherBuilds(pk, start, end, orderByComparator);
	}

	/**
	* Returns the number of patcher builds associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @return the number of patcher builds associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherBuildsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuildsSize(pk);
	}

	/**
	* Returns <code>true</code> if the patcher build is associated with the patcher account.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	*
	* @param pk the primary key of the patcher account to check for associations with patcher builds
	* @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherBuilds(pk);
	}

	/**
	* Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuild(pk, patcherBuild);
	}

	/**
	* Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuilds(pk, patcherBuilds);
	}

	/**
	* Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account to clear the associated patcher builds from
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().clearPatcherBuilds(pk);
	}

	/**
	* Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuild(pk, patcherBuild);
	}

	/**
	* Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuilds(pk, patcherBuilds);
	}

	/**
	* Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher account
	* @param patcherBuilds the patcher builds to be associated with the patcher account
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherBuilds(pk, patcherBuilds);
	}

	public static PatcherAccountPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherAccountPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherAccountPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherAccountUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherAccountPersistence persistence) {
	}

	private static PatcherAccountPersistence _persistence;
}