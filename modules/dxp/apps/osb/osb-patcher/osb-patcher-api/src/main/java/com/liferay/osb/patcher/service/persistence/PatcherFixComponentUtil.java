/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher fix component service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherFixComponentPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponentPersistence
 * @generated
 */
public class PatcherFixComponentUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(PatcherFixComponent patcherFixComponent) {
		getPersistence().clearCache(patcherFixComponent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, PatcherFixComponent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFixComponent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherFixComponent> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherFixComponent update(
		PatcherFixComponent patcherFixComponent) {

		return getPersistence().update(patcherFixComponent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherFixComponent update(
		PatcherFixComponent patcherFixComponent,
		ServiceContext serviceContext) {

		return getPersistence().update(patcherFixComponent, serviceContext);
	}

	/**
	 * Caches the patcher fix component in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponent the patcher fix component
	 */
	public static void cacheResult(PatcherFixComponent patcherFixComponent) {
		getPersistence().cacheResult(patcherFixComponent);
	}

	/**
	 * Caches the patcher fix components in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponents the patcher fix components
	 */
	public static void cacheResult(
		List<PatcherFixComponent> patcherFixComponents) {

		getPersistence().cacheResult(patcherFixComponents);
	}

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	public static PatcherFixComponent create(long patcherFixComponentId) {
		return getPersistence().create(patcherFixComponentId);
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	public static PatcherFixComponent remove(long patcherFixComponentId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherFixComponentException {

		return getPersistence().remove(patcherFixComponentId);
	}

	public static PatcherFixComponent updateImpl(
		PatcherFixComponent patcherFixComponent) {

		return getPersistence().updateImpl(patcherFixComponent);
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a <code>NoSuchPatcherFixComponentException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	public static PatcherFixComponent findByPrimaryKey(
			long patcherFixComponentId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherFixComponentException {

		return getPersistence().findByPrimaryKey(patcherFixComponentId);
	}

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 */
	public static PatcherFixComponent fetchByPrimaryKey(
		long patcherFixComponentId) {

		return getPersistence().fetchByPrimaryKey(patcherFixComponentId);
	}

	/**
	 * Returns all the patcher fix components.
	 *
	 * @return the patcher fix components
	 */
	public static List<PatcherFixComponent> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @return the range of patcher fix components
	 */
	public static List<PatcherFixComponent> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix components
	 */
	public static List<PatcherFixComponent> findAll(
		int start, int end,
		OrderByComparator<PatcherFixComponent> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix components
	 */
	public static List<PatcherFixComponent> findAll(
		int start, int end,
		OrderByComparator<PatcherFixComponent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fix components from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher fix components.
	 *
	 * @return the number of patcher fix components
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherFixComponentPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PatcherFixComponentPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PatcherFixComponentPersistence _persistence;

}