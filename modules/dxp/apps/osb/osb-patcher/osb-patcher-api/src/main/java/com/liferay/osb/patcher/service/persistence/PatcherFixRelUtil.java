/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher fix rel service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherFixRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixRelPersistence
 * @generated
 */
public class PatcherFixRelUtil {

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
	public static void clearCache(PatcherFixRel patcherFixRel) {
		getPersistence().clearCache(patcherFixRel);
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
	public static Map<Serializable, PatcherFixRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFixRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherFixRel update(PatcherFixRel patcherFixRel) {
		return getPersistence().update(patcherFixRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherFixRel update(
		PatcherFixRel patcherFixRel, ServiceContext serviceContext) {

		return getPersistence().update(patcherFixRel, serviceContext);
	}

	/**
	 * Returns all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId) {

		return getPersistence().findByChildPatcherFixId(childPatcherFixId);
	}

	/**
	 * Returns a range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end) {

		return getPersistence().findByChildPatcherFixId(
			childPatcherFixId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().findByChildPatcherFixId(
			childPatcherFixId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByChildPatcherFixId(
		long childPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByChildPatcherFixId(
			childPatcherFixId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel findByChildPatcherFixId_First(
			long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByChildPatcherFixId_First(
			childPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel fetchByChildPatcherFixId_First(
		long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().fetchByChildPatcherFixId_First(
			childPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel findByChildPatcherFixId_Last(
			long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByChildPatcherFixId_Last(
			childPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel fetchByChildPatcherFixId_Last(
		long childPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().fetchByChildPatcherFixId_Last(
			childPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where childPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param childPatcherFixId the child patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public static PatcherFixRel[] findByChildPatcherFixId_PrevAndNext(
			long patcherFixRelId, long childPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByChildPatcherFixId_PrevAndNext(
			patcherFixRelId, childPatcherFixId, orderByComparator);
	}

	/**
	 * Removes all the patcher fix rels where childPatcherFixId = &#63; from the database.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 */
	public static void removeByChildPatcherFixId(long childPatcherFixId) {
		getPersistence().removeByChildPatcherFixId(childPatcherFixId);
	}

	/**
	 * Returns the number of patcher fix rels where childPatcherFixId = &#63;.
	 *
	 * @param childPatcherFixId the child patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	public static int countByChildPatcherFixId(long childPatcherFixId) {
		return getPersistence().countByChildPatcherFixId(childPatcherFixId);
	}

	/**
	 * Returns all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId) {

		return getPersistence().findByParentPatcherFixId(parentPatcherFixId);
	}

	/**
	 * Returns a range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end) {

		return getPersistence().findByParentPatcherFixId(
			parentPatcherFixId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().findByParentPatcherFixId(
			parentPatcherFixId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix rels
	 */
	public static List<PatcherFixRel> findByParentPatcherFixId(
		long parentPatcherFixId, int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentPatcherFixId(
			parentPatcherFixId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel findByParentPatcherFixId_First(
			long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByParentPatcherFixId_First(
			parentPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the first patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel fetchByParentPatcherFixId_First(
		long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().fetchByParentPatcherFixId_First(
			parentPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel findByParentPatcherFixId_Last(
			long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByParentPatcherFixId_Last(
			parentPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix rel, or <code>null</code> if a matching patcher fix rel could not be found
	 */
	public static PatcherFixRel fetchByParentPatcherFixId_Last(
		long parentPatcherFixId,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().fetchByParentPatcherFixId_Last(
			parentPatcherFixId, orderByComparator);
	}

	/**
	 * Returns the patcher fix rels before and after the current patcher fix rel in the ordered set where parentPatcherFixId = &#63;.
	 *
	 * @param patcherFixRelId the primary key of the current patcher fix rel
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public static PatcherFixRel[] findByParentPatcherFixId_PrevAndNext(
			long patcherFixRelId, long parentPatcherFixId,
			OrderByComparator<PatcherFixRel> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByParentPatcherFixId_PrevAndNext(
			patcherFixRelId, parentPatcherFixId, orderByComparator);
	}

	/**
	 * Removes all the patcher fix rels where parentPatcherFixId = &#63; from the database.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 */
	public static void removeByParentPatcherFixId(long parentPatcherFixId) {
		getPersistence().removeByParentPatcherFixId(parentPatcherFixId);
	}

	/**
	 * Returns the number of patcher fix rels where parentPatcherFixId = &#63;.
	 *
	 * @param parentPatcherFixId the parent patcher fix ID
	 * @return the number of matching patcher fix rels
	 */
	public static int countByParentPatcherFixId(long parentPatcherFixId) {
		return getPersistence().countByParentPatcherFixId(parentPatcherFixId);
	}

	/**
	 * Caches the patcher fix rel in the entity cache if it is enabled.
	 *
	 * @param patcherFixRel the patcher fix rel
	 */
	public static void cacheResult(PatcherFixRel patcherFixRel) {
		getPersistence().cacheResult(patcherFixRel);
	}

	/**
	 * Caches the patcher fix rels in the entity cache if it is enabled.
	 *
	 * @param patcherFixRels the patcher fix rels
	 */
	public static void cacheResult(List<PatcherFixRel> patcherFixRels) {
		getPersistence().cacheResult(patcherFixRels);
	}

	/**
	 * Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	 *
	 * @param patcherFixRelId the primary key for the new patcher fix rel
	 * @return the new patcher fix rel
	 */
	public static PatcherFixRel create(long patcherFixRelId) {
		return getPersistence().create(patcherFixRelId);
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public static PatcherFixRel remove(long patcherFixRelId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().remove(patcherFixRelId);
	}

	public static PatcherFixRel updateImpl(PatcherFixRel patcherFixRel) {
		return getPersistence().updateImpl(patcherFixRel);
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a <code>NoSuchPatcherFixRelException</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 */
	public static PatcherFixRel findByPrimaryKey(long patcherFixRelId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException {

		return getPersistence().findByPrimaryKey(patcherFixRelId);
	}

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 */
	public static PatcherFixRel fetchByPrimaryKey(long patcherFixRelId) {
		return getPersistence().fetchByPrimaryKey(patcherFixRelId);
	}

	/**
	 * Returns all the patcher fix rels.
	 *
	 * @return the patcher fix rels
	 */
	public static List<PatcherFixRel> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of patcher fix rels
	 */
	public static List<PatcherFixRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix rels
	 */
	public static List<PatcherFixRel> findAll(
		int start, int end,
		OrderByComparator<PatcherFixRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix rels
	 */
	public static List<PatcherFixRel> findAll(
		int start, int end, OrderByComparator<PatcherFixRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fix rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher fix rels.
	 *
	 * @return the number of patcher fix rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherFixRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherFixRelPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherFixRelPersistence _persistence;

}