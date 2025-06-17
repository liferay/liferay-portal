/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher build rel service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherBuildRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildRelPersistence
 * @generated
 */
public class PatcherBuildRelUtil {

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
	public static void clearCache(PatcherBuildRel patcherBuildRel) {
		getPersistence().clearCache(patcherBuildRel);
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
	public static Map<Serializable, PatcherBuildRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherBuildRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherBuildRel update(PatcherBuildRel patcherBuildRel) {
		return getPersistence().update(patcherBuildRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherBuildRel update(
		PatcherBuildRel patcherBuildRel, ServiceContext serviceContext) {

		return getPersistence().update(patcherBuildRel, serviceContext);
	}

	/**
	 * Caches the patcher build rel in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRel the patcher build rel
	 */
	public static void cacheResult(PatcherBuildRel patcherBuildRel) {
		getPersistence().cacheResult(patcherBuildRel);
	}

	/**
	 * Caches the patcher build rels in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRels the patcher build rels
	 */
	public static void cacheResult(List<PatcherBuildRel> patcherBuildRels) {
		getPersistence().cacheResult(patcherBuildRels);
	}

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	public static PatcherBuildRel create(long patcherBuildRelId) {
		return getPersistence().create(patcherBuildRelId);
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public static PatcherBuildRel remove(long patcherBuildRelId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherBuildRelException {

		return getPersistence().remove(patcherBuildRelId);
	}

	public static PatcherBuildRel updateImpl(PatcherBuildRel patcherBuildRel) {
		return getPersistence().updateImpl(patcherBuildRel);
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a <code>NoSuchPatcherBuildRelException</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	public static PatcherBuildRel findByPrimaryKey(long patcherBuildRelId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherBuildRelException {

		return getPersistence().findByPrimaryKey(patcherBuildRelId);
	}

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 */
	public static PatcherBuildRel fetchByPrimaryKey(long patcherBuildRelId) {
		return getPersistence().fetchByPrimaryKey(patcherBuildRelId);
	}

	/**
	 * Returns all the patcher build rels.
	 *
	 * @return the patcher build rels
	 */
	public static List<PatcherBuildRel> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of patcher build rels
	 */
	public static List<PatcherBuildRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher build rels
	 */
	public static List<PatcherBuildRel> findAll(
		int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher build rels
	 */
	public static List<PatcherBuildRel> findAll(
		int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher build rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher build rels.
	 *
	 * @return the number of patcher build rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherBuildRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherBuildRelPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherBuildRelPersistence _persistence;

}