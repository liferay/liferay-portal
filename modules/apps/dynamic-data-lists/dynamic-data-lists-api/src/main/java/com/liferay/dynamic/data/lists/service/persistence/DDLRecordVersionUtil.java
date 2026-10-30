/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence;

import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddl record version service. This utility wraps <code>com.liferay.dynamic.data.lists.service.persistence.impl.DDLRecordVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordVersionPersistence
 * @generated
 */
public class DDLRecordVersionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DDLRecordVersion> ddlRecordVersions) {
		getPersistence().cacheResult(ddlRecordVersions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DDLRecordVersion ddlRecordVersion) {
		getPersistence().cacheResult(ddlRecordVersion);
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
	public static void clearCache(DDLRecordVersion ddlRecordVersion) {
		getPersistence().clearCache(ddlRecordVersion);
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
	public static Map<Serializable, DDLRecordVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDLRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDLRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDLRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDLRecordVersion update(DDLRecordVersion ddlRecordVersion) {
		return getPersistence().update(ddlRecordVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDLRecordVersion update(
		DDLRecordVersion ddlRecordVersion, ServiceContext serviceContext) {

		return getPersistence().update(ddlRecordVersion, serviceContext);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByRecordId(
			recordId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion findByRecordId_First(
			long recordId,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().findByRecordId_First(
			recordId, orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion fetchByRecordId_First(
		long recordId, OrderByComparator<DDLRecordVersion> orderByComparator) {

		return getPersistence().fetchByRecordId_First(
			recordId, orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where recordId = &#63; from the database.
	 *
	 * @param recordId the record ID
	 */
	public static void removeByRecordId(long recordId) {
		getPersistence().removeByRecordId(recordId);
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @return the number of matching ddl record versions
	 */
	public static int countByRecordId(long recordId) {
		return getPersistence().countByRecordId(recordId);
	}

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion findByR_V(long recordId, String version)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().findByR_V(recordId, version);
	}

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion fetchByR_V(
		long recordId, String version, boolean useFinderCache) {

		return getPersistence().fetchByR_V(recordId, version, useFinderCache);
	}

	/**
	 * Removes the ddl record version where recordId = &#63; and version = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the ddl record version that was removed
	 */
	public static DDLRecordVersion removeByR_V(long recordId, String version)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().removeByR_V(recordId, version);
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and version = &#63;.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the number of matching ddl record versions
	 */
	public static int countByR_V(long recordId, String version) {
		return getPersistence().countByR_V(recordId, version);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_S(
			recordId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion findByR_S_First(
			long recordId, int status,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().findByR_S_First(
			recordId, status, orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion fetchByR_S_First(
		long recordId, int status,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return getPersistence().fetchByR_S_First(
			recordId, status, orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where recordId = &#63; and status = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 */
	public static void removeByR_S(long recordId, int status) {
		getPersistence().removeByR_S(recordId, status);
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @return the number of matching ddl record versions
	 */
	public static int countByR_S(long recordId, int status) {
		return getPersistence().countByR_S(recordId, status);
	}

	/**
	 * Creates a new ddl record version with the primary key. Does not add the ddl record version to the database.
	 *
	 * @param recordVersionId the primary key for the new ddl record version
	 * @return the new ddl record version
	 */
	public static DDLRecordVersion create(long recordVersionId) {
		return getPersistence().create(recordVersionId);
	}

	/**
	 * Removes the ddl record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version that was removed
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	public static DDLRecordVersion remove(long recordVersionId)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().remove(recordVersionId);
	}

	public static DDLRecordVersion updateImpl(
		DDLRecordVersion ddlRecordVersion) {

		return getPersistence().updateImpl(ddlRecordVersion);
	}

	/**
	 * Returns the ddl record version with the primary key or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	public static DDLRecordVersion findByPrimaryKey(long recordVersionId)
		throws com.liferay.dynamic.data.lists.exception.
			NoSuchRecordVersionException {

		return getPersistence().findByPrimaryKey(recordVersionId);
	}

	/**
	 * Returns the ddl record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version, or <code>null</code> if a ddl record version with the primary key could not be found
	 */
	public static DDLRecordVersion fetchByPrimaryKey(long recordVersionId) {
		return getPersistence().fetchByPrimaryKey(recordVersionId);
	}

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	public static DDLRecordVersion fetchByR_V(long recordId, String version) {
		return getPersistence().fetchByR_V(recordId, version);
	}

	/**
	 * Returns all the ddl record versions where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @return the matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByRecordId(long recordId) {
		return getPersistence().findByRecordId(recordId);
	}

	/**
	 * Returns a range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @return the range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end) {

		return getPersistence().findByRecordId(recordId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return getPersistence().findByRecordId(
			recordId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @return the matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByR_S(long recordId, int status) {
		return getPersistence().findByR_S(recordId, status);
	}

	/**
	 * Returns a range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @return the range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end) {

		return getPersistence().findByR_S(recordId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record versions
	 */
	public static List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return getPersistence().findByR_S(
			recordId, status, start, end, orderByComparator);
	}

	public static DDLRecordVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DDLRecordVersionPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DDLRecordVersionPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-821823446