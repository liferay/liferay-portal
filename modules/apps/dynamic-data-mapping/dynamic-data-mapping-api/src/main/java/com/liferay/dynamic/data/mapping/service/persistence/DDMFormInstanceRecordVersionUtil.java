/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddm form instance record version service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMFormInstanceRecordVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordVersionPersistence
 * @generated
 */
public class DDMFormInstanceRecordVersionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<DDMFormInstanceRecordVersion> ddmFormInstanceRecordVersions) {

		getPersistence().cacheResult(ddmFormInstanceRecordVersions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		getPersistence().cacheResult(ddmFormInstanceRecordVersion);
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
	public static void clearCache(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		getPersistence().clearCache(ddmFormInstanceRecordVersion);
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
	public static Map<Serializable, DDMFormInstanceRecordVersion>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMFormInstanceRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMFormInstanceRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMFormInstanceRecordVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMFormInstanceRecordVersion update(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		return getPersistence().update(ddmFormInstanceRecordVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMFormInstanceRecordVersion update(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion,
		ServiceContext serviceContext) {

		return getPersistence().update(
			ddmFormInstanceRecordVersion, serviceContext);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByFormInstanceRecordId(
		long formInstanceRecordId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFormInstanceRecordId(
			formInstanceRecordId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion findByFormInstanceRecordId_First(
			long formInstanceRecordId,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByFormInstanceRecordId_First(
			formInstanceRecordId, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion
		fetchByFormInstanceRecordId_First(
			long formInstanceRecordId,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().fetchByFormInstanceRecordId_First(
			formInstanceRecordId, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 */
	public static void removeByFormInstanceRecordId(long formInstanceRecordId) {
		getPersistence().removeByFormInstanceRecordId(formInstanceRecordId);
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @return the number of matching ddm form instance record versions
	 */
	public static int countByFormInstanceRecordId(long formInstanceRecordId) {
		return getPersistence().countByFormInstanceRecordId(
			formInstanceRecordId);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_F(
			userId, formInstanceId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion findByU_F_First(
			long userId, long formInstanceId,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByU_F_First(
			userId, formInstanceId, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByU_F_First(
		long userId, long formInstanceId,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().fetchByU_F_First(
			userId, formInstanceId, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 */
	public static void removeByU_F(long userId, long formInstanceId) {
		getPersistence().removeByU_F(userId, formInstanceId);
	}

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance record versions
	 */
	public static int countByU_F(long userId, long formInstanceId) {
		return getPersistence().countByU_F(userId, formInstanceId);
	}

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion findByF_V(
			long formInstanceRecordId, String version)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByF_V(formInstanceRecordId, version);
	}

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByF_V(
		long formInstanceRecordId, String version, boolean useFinderCache) {

		return getPersistence().fetchByF_V(
			formInstanceRecordId, version, useFinderCache);
	}

	/**
	 * Removes the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the ddm form instance record version that was removed
	 */
	public static DDMFormInstanceRecordVersion removeByF_V(
			long formInstanceRecordId, String version)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().removeByF_V(formInstanceRecordId, version);
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and version = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the number of matching ddm form instance record versions
	 */
	public static int countByF_V(long formInstanceRecordId, String version) {
		return getPersistence().countByF_V(formInstanceRecordId, version);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByF_S(
			formInstanceRecordId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion findByF_S_First(
			long formInstanceRecordId, int status,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByF_S_First(
			formInstanceRecordId, status, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByF_S_First(
		long formInstanceRecordId, int status,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().fetchByF_S_First(
			formInstanceRecordId, status, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 */
	public static void removeByF_S(long formInstanceRecordId, int status) {
		getPersistence().removeByF_S(formInstanceRecordId, status);
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	public static int countByF_S(long formInstanceRecordId, int status) {
		return getPersistence().countByF_S(formInstanceRecordId, status);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion findByU_F_F_S_First(
			long userId, long formInstanceId, String formInstanceVersion,
			int status,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByU_F_F_S_First(
			userId, formInstanceId, formInstanceVersion, status,
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByU_F_F_S_First(
		long userId, long formInstanceId, String formInstanceVersion,
		int status,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().fetchByU_F_F_S_First(
			userId, formInstanceId, formInstanceVersion, status,
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 */
	public static void removeByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		getPersistence().removeByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status);
	}

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	public static int countByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		return getPersistence().countByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status);
	}

	/**
	 * Creates a new ddm form instance record version with the primary key. Does not add the ddm form instance record version to the database.
	 *
	 * @param formInstanceRecordVersionId the primary key for the new ddm form instance record version
	 * @return the new ddm form instance record version
	 */
	public static DDMFormInstanceRecordVersion create(
		long formInstanceRecordVersionId) {

		return getPersistence().create(formInstanceRecordVersionId);
	}

	/**
	 * Removes the ddm form instance record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version that was removed
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	public static DDMFormInstanceRecordVersion remove(
			long formInstanceRecordVersionId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().remove(formInstanceRecordVersionId);
	}

	public static DDMFormInstanceRecordVersion updateImpl(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		return getPersistence().updateImpl(ddmFormInstanceRecordVersion);
	}

	/**
	 * Returns the ddm form instance record version with the primary key or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	public static DDMFormInstanceRecordVersion findByPrimaryKey(
			long formInstanceRecordVersionId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordVersionException {

		return getPersistence().findByPrimaryKey(formInstanceRecordVersionId);
	}

	/**
	 * Returns the ddm form instance record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version, or <code>null</code> if a ddm form instance record version with the primary key could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByPrimaryKey(
		long formInstanceRecordVersionId) {

		return getPersistence().fetchByPrimaryKey(formInstanceRecordVersionId);
	}

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	public static DDMFormInstanceRecordVersion fetchByF_V(
		long formInstanceRecordId, String version) {

		return getPersistence().fetchByF_V(formInstanceRecordId, version);
	}

	/**
	 * Returns all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @return the matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByFormInstanceRecordId(
		long formInstanceRecordId) {

		return getPersistence().findByFormInstanceRecordId(
			formInstanceRecordId);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByFormInstanceRecordId(
		long formInstanceRecordId, int start, int end) {

		return getPersistence().findByFormInstanceRecordId(
			formInstanceRecordId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByFormInstanceRecordId(
		long formInstanceRecordId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().findByFormInstanceRecordId(
			formInstanceRecordId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId) {

		return getPersistence().findByU_F(userId, formInstanceId);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end) {

		return getPersistence().findByU_F(userId, formInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().findByU_F(
			userId, formInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @return the matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status) {

		return getPersistence().findByF_S(formInstanceRecordId, status);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end) {

		return getPersistence().findByF_S(
			formInstanceRecordId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().findByF_S(
			formInstanceRecordId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @return the matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		return getPersistence().findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status);
	}

	/**
	 * Returns a range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end) {

		return getPersistence().findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getPersistence().findByU_F_F_S(
			userId, formInstanceId, formInstanceVersion, status, start, end,
			orderByComparator);
	}

	public static DDMFormInstanceRecordVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		DDMFormInstanceRecordVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile DDMFormInstanceRecordVersionPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1519941377