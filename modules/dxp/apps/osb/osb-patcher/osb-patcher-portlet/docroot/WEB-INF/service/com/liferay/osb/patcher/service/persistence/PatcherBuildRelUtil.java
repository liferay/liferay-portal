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

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher build rel service. This utility wraps {@link PatcherBuildRelPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildRelPersistence
 * @see PatcherBuildRelPersistenceImpl
 * @generated
 */
public class PatcherBuildRelUtil {
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
	public static void clearCache(PatcherBuildRel patcherBuildRel) {
		getPersistence().clearCache(patcherBuildRel);
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
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherBuildRel update(PatcherBuildRel patcherBuildRel)
		throws SystemException {
		return getPersistence().update(patcherBuildRel);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherBuildRel update(PatcherBuildRel patcherBuildRel,
		ServiceContext serviceContext) throws SystemException {
		return getPersistence().update(patcherBuildRel, serviceContext);
	}

	/**
	* Caches the patcher build rel in the entity cache if it is enabled.
	*
	* @param patcherBuildRel the patcher build rel
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel) {
		getPersistence().cacheResult(patcherBuildRel);
	}

	/**
	* Caches the patcher build rels in the entity cache if it is enabled.
	*
	* @param patcherBuildRels the patcher build rels
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> patcherBuildRels) {
		getPersistence().cacheResult(patcherBuildRels);
	}

	/**
	* Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	*
	* @param patcherBuildRelId the primary key for the new patcher build rel
	* @return the new patcher build rel
	*/
	public static com.liferay.osb.patcher.model.PatcherBuildRel create(
		long patcherBuildRelId) {
		return getPersistence().create(patcherBuildRelId);
	}

	/**
	* Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuildRel remove(
		long patcherBuildRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherBuildRelId);
	}

	public static com.liferay.osb.patcher.model.PatcherBuildRel updateImpl(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherBuildRel);
	}

	/**
	* Returns the patcher build rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildRelException} if it could not be found.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel
	* @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuildRel findByPrimaryKey(
		long patcherBuildRelId)
		throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherBuildRelId);
	}

	/**
	* Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherBuildRelId the primary key of the patcher build rel
	* @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherBuildRel fetchByPrimaryKey(
		long patcherBuildRelId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherBuildRelId);
	}

	/**
	* Returns all the patcher build rels.
	*
	* @return the patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

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
	public static java.util.List<com.liferay.osb.patcher.model.PatcherBuildRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher build rels from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher build rels.
	*
	* @return the number of patcher build rels
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherBuildRelPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherBuildRelPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherBuildRelPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherBuildRelUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherBuildRelPersistence persistence) {
	}

	private static PatcherBuildRelPersistence _persistence;
}