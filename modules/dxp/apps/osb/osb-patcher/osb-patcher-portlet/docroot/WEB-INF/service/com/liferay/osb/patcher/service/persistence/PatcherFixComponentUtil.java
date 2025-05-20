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

import com.liferay.osb.patcher.model.PatcherFixComponent;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.ServiceContext;

import java.util.List;

/**
 * The persistence utility for the patcher fix component service. This utility wraps {@link PatcherFixComponentPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixComponentPersistence
 * @see PatcherFixComponentPersistenceImpl
 * @generated
 */
public class PatcherFixComponentUtil {
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
	public static void clearCache(PatcherFixComponent patcherFixComponent) {
		getPersistence().clearCache(patcherFixComponent);
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
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end)
		throws SystemException {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel)
	 */
	public static PatcherFixComponent update(
		PatcherFixComponent patcherFixComponent) throws SystemException {
		return getPersistence().update(patcherFixComponent);
	}

	/**
	 * @see com.liferay.portal.service.persistence.BasePersistence#update(com.liferay.portal.model.BaseModel, ServiceContext)
	 */
	public static PatcherFixComponent update(
		PatcherFixComponent patcherFixComponent, ServiceContext serviceContext)
		throws SystemException {
		return getPersistence().update(patcherFixComponent, serviceContext);
	}

	/**
	* Caches the patcher fix component in the entity cache if it is enabled.
	*
	* @param patcherFixComponent the patcher fix component
	*/
	public static void cacheResult(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent) {
		getPersistence().cacheResult(patcherFixComponent);
	}

	/**
	* Caches the patcher fix components in the entity cache if it is enabled.
	*
	* @param patcherFixComponents the patcher fix components
	*/
	public static void cacheResult(
		java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> patcherFixComponents) {
		getPersistence().cacheResult(patcherFixComponents);
	}

	/**
	* Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	*
	* @param patcherFixComponentId the primary key for the new patcher fix component
	* @return the new patcher fix component
	*/
	public static com.liferay.osb.patcher.model.PatcherFixComponent create(
		long patcherFixComponentId) {
		return getPersistence().create(patcherFixComponentId);
	}

	/**
	* Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component that was removed
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixComponent remove(
		long patcherFixComponentId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().remove(patcherFixComponentId);
	}

	public static com.liferay.osb.patcher.model.PatcherFixComponent updateImpl(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().updateImpl(patcherFixComponent);
	}

	/**
	* Returns the patcher fix component with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixComponentException} if it could not be found.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component
	* @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixComponent findByPrimaryKey(
		long patcherFixComponentId)
		throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException,
			com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findByPrimaryKey(patcherFixComponentId);
	}

	/**
	* Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param patcherFixComponentId the primary key of the patcher fix component
	* @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	* @throws SystemException if a system exception occurred
	*/
	public static com.liferay.osb.patcher.model.PatcherFixComponent fetchByPrimaryKey(
		long patcherFixComponentId)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().fetchByPrimaryKey(patcherFixComponentId);
	}

	/**
	* Returns all the patcher fix components.
	*
	* @return the patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the patcher fix components.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix components
	* @param end the upper bound of the range of patcher fix components (not inclusive)
	* @return the range of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll(
		int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the patcher fix components.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of patcher fix components
	* @param end the upper bound of the range of patcher fix components (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public static java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Removes all the patcher fix components from the database.
	*
	* @throws SystemException if a system exception occurred
	*/
	public static void removeAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of patcher fix components.
	*
	* @return the number of patcher fix components
	* @throws SystemException if a system exception occurred
	*/
	public static int countAll()
		throws com.liferay.portal.kernel.exception.SystemException {
		return getPersistence().countAll();
	}

	public static PatcherFixComponentPersistence getPersistence() {
		if (_persistence == null) {
			_persistence = (PatcherFixComponentPersistence)PortletBeanLocatorUtil.locate(com.liferay.osb.patcher.service.ClpSerializer.getServletContextName(),
					PatcherFixComponentPersistence.class.getName());

			ReferenceRegistry.registerReference(PatcherFixComponentUtil.class,
				"_persistence");
		}

		return _persistence;
	}

	/**
	 * @deprecated As of 6.2.0
	 */
	public void setPersistence(PatcherFixComponentPersistence persistence) {
	}

	private static PatcherFixComponentPersistence _persistence;
}