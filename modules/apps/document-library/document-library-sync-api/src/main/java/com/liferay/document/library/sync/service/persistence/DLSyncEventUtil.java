/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.service.persistence;

import com.liferay.document.library.sync.model.DLSyncEvent;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the dl sync event service. This utility wraps <code>com.liferay.document.library.sync.service.persistence.impl.DLSyncEventPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLSyncEventPersistence
 * @generated
 */
public class DLSyncEventUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DLSyncEvent> dlSyncEvents) {
		getPersistence().cacheResult(dlSyncEvents);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DLSyncEvent dlSyncEvent) {
		getPersistence().cacheResult(dlSyncEvent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(DLSyncEvent dlSyncEvent) {
		getPersistence().clearCache(dlSyncEvent);
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
	public static Map<Serializable, DLSyncEvent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DLSyncEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DLSyncEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DLSyncEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DLSyncEvent> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DLSyncEvent update(DLSyncEvent dlSyncEvent) {
		return getPersistence().update(dlSyncEvent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DLSyncEvent update(
		DLSyncEvent dlSyncEvent, ServiceContext serviceContext) {

		return getPersistence().update(dlSyncEvent, serviceContext);
	}

	/**
	 * Returns the dl sync event where typePK = &#63; or throws a <code>NoSuchEventException</code> if it could not be found.
	 *
	 * @param typePK the type pk
	 * @return the matching dl sync event
	 * @throws NoSuchEventException if a matching dl sync event could not be found
	 */
	public static DLSyncEvent findByTypePK(long typePK)
		throws com.liferay.document.library.sync.exception.
			NoSuchEventException {

		return getPersistence().findByTypePK(typePK);
	}

	/**
	 * Returns the dl sync event where typePK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param typePK the type pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl sync event, or <code>null</code> if a matching dl sync event could not be found
	 */
	public static DLSyncEvent fetchByTypePK(
		long typePK, boolean useFinderCache) {

		return getPersistence().fetchByTypePK(typePK, useFinderCache);
	}

	/**
	 * Removes the dl sync event where typePK = &#63; from the database.
	 *
	 * @param typePK the type pk
	 * @return the dl sync event that was removed
	 */
	public static DLSyncEvent removeByTypePK(long typePK)
		throws com.liferay.document.library.sync.exception.
			NoSuchEventException {

		return getPersistence().removeByTypePK(typePK);
	}

	/**
	 * Returns the number of dl sync events where typePK = &#63;.
	 *
	 * @param typePK the type pk
	 * @return the number of matching dl sync events
	 */
	public static int countByTypePK(long typePK) {
		return getPersistence().countByTypePK(typePK);
	}

	/**
	 * Creates a new dl sync event with the primary key. Does not add the dl sync event to the database.
	 *
	 * @param syncEventId the primary key for the new dl sync event
	 * @return the new dl sync event
	 */
	public static DLSyncEvent create(long syncEventId) {
		return getPersistence().create(syncEventId);
	}

	/**
	 * Removes the dl sync event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event that was removed
	 * @throws NoSuchEventException if a dl sync event with the primary key could not be found
	 */
	public static DLSyncEvent remove(long syncEventId)
		throws com.liferay.document.library.sync.exception.
			NoSuchEventException {

		return getPersistence().remove(syncEventId);
	}

	public static DLSyncEvent updateImpl(DLSyncEvent dlSyncEvent) {
		return getPersistence().updateImpl(dlSyncEvent);
	}

	/**
	 * Returns the dl sync event with the primary key or throws a <code>NoSuchEventException</code> if it could not be found.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event
	 * @throws NoSuchEventException if a dl sync event with the primary key could not be found
	 */
	public static DLSyncEvent findByPrimaryKey(long syncEventId)
		throws com.liferay.document.library.sync.exception.
			NoSuchEventException {

		return getPersistence().findByPrimaryKey(syncEventId);
	}

	/**
	 * Returns the dl sync event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event, or <code>null</code> if a dl sync event with the primary key could not be found
	 */
	public static DLSyncEvent fetchByPrimaryKey(long syncEventId) {
		return getPersistence().fetchByPrimaryKey(syncEventId);
	}

	/**
	 * Returns the dl sync event where typePK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param typePK the type pk
	 * @return the matching dl sync event, or <code>null</code> if a matching dl sync event could not be found
	 */
	public static DLSyncEvent fetchByTypePK(long typePK) {
		return getPersistence().fetchByTypePK(typePK);
	}

	public static DLSyncEventPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DLSyncEventPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DLSyncEventPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:239556952