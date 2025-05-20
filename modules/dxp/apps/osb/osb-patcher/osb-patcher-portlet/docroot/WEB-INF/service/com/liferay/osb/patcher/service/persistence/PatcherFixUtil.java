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

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher fix service. This utility wraps {@link PatcherFixPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPersistence
 * @see PatcherFixPersistenceImpl
 * @generated
 */
public class PatcherFixUtil {
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
	public static void clearCache(PatcherFix patcherFix) {
		getPersistence().clearCache(patcherFix);
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
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherFix update(PatcherFix patcherFix)
		throws SystemException {
		return getPersistence().update(patcherFix);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherFix update(PatcherFix patcherFix,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherFix, serviceContext);
	}

	/**
	* Caches the patcher fix in the entity cache if it is enabled.
	*
	* @param patcherFix the patcher fix
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherFix patcherFix) {
		getPersistence().cacheResult(patcherFix);
	}

	/**
	* Caches the patcher fixs in the entity cache if it is enabled.
	*
	* @param patcherFixs the patcher fixs
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs) {
		getPersistence().cacheResult(patcherFixs);
	}

	/**
	* Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	*
	* @param patcherFixId the primary key for the new patcher fix
	* @return the new patcher fix
	*/
	public static com.liferay.osb.patcher.model.PatcherFix create(
		long patcherFixId) {
		return getPersistence().create(patcherFixId);
	}

	/**
	* Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix remove(
		long patcherFixId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherFixId);
	}

	public static com.liferay.osb.patcher.model.PatcherFix updateImpl(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherFix);
	}

	/**
	* Returns the patcher fix with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixException} if it could not be found.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix findByPrimaryKey(
		long patcherFixId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherFixId);
	}

	/**
	* Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixId the primary key of the patcher fix
	* @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFix fetchByPrimaryKey(
		long patcherFixId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherFixId);
	}

	/**
	* Returns all the patcher fixs.
	*
	* @return the patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFix> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher fixs from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher fixs.
	*
	* @return the number of patcher fixs
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	/**
	* Returns all the patcher builds associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuilds(pk);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuilds(pk, start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .getPatcherBuilds(pk, start, end, orderByComparator);
	}

	/**
	* Returns the number of patcher builds associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the number of patcher builds associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherBuildsSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherBuildsSize(pk);
	}

	/**
	* Returns <code>true</code> if the patcher build is associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	*
	* @param pk the primary key of the patcher fix to check for associations with patcher builds
	* @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherBuilds(pk);
	}

	/**
	* Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuild(pk, patcherBuild);
	}

	/**
	* Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherBuilds(pk, patcherBuilds);
	}

	/**
	* Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix to clear the associated patcher builds from
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherBuilds(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().clearPatcherBuilds(pk);
	}

	/**
	* Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPK the primary key of the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuild(long pk, long patcherBuildPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuild(pk, patcherBuildPK);
	}

	/**
	* Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuild the patcher build
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuild(pk, patcherBuild);
	}

	/**
	* Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherBuilds(pk, patcherBuilds);
	}

	/**
	* Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	* Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherBuilds the patcher builds to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherBuilds(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherBuilds(pk, patcherBuilds);
	}

	/**
	* Returns all the patcher fix packs associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk) throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixPacks(pk);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixPacks(pk, start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence()
				   .getPatcherFixPacks(pk, start, end, orderByComparator);
	}

	/**
	* Returns the number of patcher fix packs associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @return the number of patcher fix packs associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static int getPatcherFixPacksSize(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().getPatcherFixPacksSize(pk);
	}

	/**
	* Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	* Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	*
	* @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	* @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	* @throws SystemException if a system exception occurred
	*/
	public static boolean containsPatcherFixPacks(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().containsPatcherFixPacks(pk);
	}

	/**
	* Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	* Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPack the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixPack(pk, patcherFixPack);
	}

	/**
	* Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	* Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public static void addPatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().addPatcherFixPacks(pk, patcherFixPacks);
	}

	/**
	* Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	* @throws SystemException if a system exception occurred
	*/
	public static void clearPatcherFixPacks(long pk)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().clearPatcherFixPacks(pk);
	}

	/**
	* Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPK the primary key of the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixPack(long pk, long patcherFixPackPK)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	* Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPack the patcher fix pack
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixPack(pk, patcherFixPack);
	}

	/**
	* Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	* Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs
	* @throws SystemException if a system exception occurred
	*/
	public static void removePatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removePatcherFixPacks(pk, patcherFixPacks);
	}

	/**
	* Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	* Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	*
	* @param pk the primary key of the patcher fix
	* @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	* @throws SystemException if a system exception occurred
	*/
	public static void setPatcherFixPacks(long pk,
		java.util.List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().setPatcherFixPacks(pk, patcherFixPacks);
	}

	public static PatcherFixPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherFixPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherFixPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherFixUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherFixPersistence persistence) {
	}

	private static PatcherFixPersistence _persistence;
}