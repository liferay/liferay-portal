/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher ticket hint service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherTicketHintPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherTicketHintPersistence
 * @generated
 */
public class PatcherTicketHintUtil {

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
	public static void clearCache(PatcherTicketHint patcherTicketHint) {
		getPersistence().clearCache(patcherTicketHint);
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
	public static Map<Serializable, PatcherTicketHint> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherTicketHint> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherTicketHint> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherTicketHint update(
		PatcherTicketHint patcherTicketHint) {

		return getPersistence().update(patcherTicketHint);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherTicketHint update(
		PatcherTicketHint patcherTicketHint, ServiceContext serviceContext) {

		return getPersistence().update(patcherTicketHint, serviceContext);
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	 */
	public static PatcherTicketHint findByPatcherProductVersionId(
			long patcherProductVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherTicketHintException {

		return getPersistence().findByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 */
	public static PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId) {

		return getPersistence().fetchByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 */
	public static PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean useFinderCache) {

		return getPersistence().fetchByPatcherProductVersionId(
			patcherProductVersionId, useFinderCache);
	}

	/**
	 * Removes the patcher ticket hint where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the patcher ticket hint that was removed
	 */
	public static PatcherTicketHint removeByPatcherProductVersionId(
			long patcherProductVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherTicketHintException {

		return getPersistence().removeByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher ticket hints
	 */
	public static int countByPatcherProductVersionId(
		long patcherProductVersionId) {

		return getPersistence().countByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Caches the patcher ticket hint in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 */
	public static void cacheResult(PatcherTicketHint patcherTicketHint) {
		getPersistence().cacheResult(patcherTicketHint);
	}

	/**
	 * Caches the patcher ticket hints in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHints the patcher ticket hints
	 */
	public static void cacheResult(List<PatcherTicketHint> patcherTicketHints) {
		getPersistence().cacheResult(patcherTicketHints);
	}

	/**
	 * Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	 *
	 * @param patcherTicketHintId the primary key for the new patcher ticket hint
	 * @return the new patcher ticket hint
	 */
	public static PatcherTicketHint create(long patcherTicketHintId) {
		return getPersistence().create(patcherTicketHintId);
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	public static PatcherTicketHint remove(long patcherTicketHintId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherTicketHintException {

		return getPersistence().remove(patcherTicketHintId);
	}

	public static PatcherTicketHint updateImpl(
		PatcherTicketHint patcherTicketHint) {

		return getPersistence().updateImpl(patcherTicketHint);
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	public static PatcherTicketHint findByPrimaryKey(long patcherTicketHintId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherTicketHintException {

		return getPersistence().findByPrimaryKey(patcherTicketHintId);
	}

	/**
	 * Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	 */
	public static PatcherTicketHint fetchByPrimaryKey(
		long patcherTicketHintId) {

		return getPersistence().fetchByPrimaryKey(patcherTicketHintId);
	}

	/**
	 * Returns all the patcher ticket hints.
	 *
	 * @return the patcher ticket hints
	 */
	public static List<PatcherTicketHint> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @return the range of patcher ticket hints
	 */
	public static List<PatcherTicketHint> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher ticket hints
	 */
	public static List<PatcherTicketHint> findAll(
		int start, int end,
		OrderByComparator<PatcherTicketHint> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher ticket hints
	 */
	public static List<PatcherTicketHint> findAll(
		int start, int end,
		OrderByComparator<PatcherTicketHint> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher ticket hints from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher ticket hints.
	 *
	 * @return the number of patcher ticket hints
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherTicketHintPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PatcherTicketHintPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PatcherTicketHintPersistence _persistence;

}