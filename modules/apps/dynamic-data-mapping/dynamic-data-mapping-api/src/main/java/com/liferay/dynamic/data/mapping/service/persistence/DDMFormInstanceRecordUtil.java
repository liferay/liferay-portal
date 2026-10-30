/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddm form instance record service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMFormInstanceRecordPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordPersistence
 * @generated
 */
public class DDMFormInstanceRecordUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<DDMFormInstanceRecord> ddmFormInstanceRecords) {

		getPersistence().cacheResult(ddmFormInstanceRecords);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		getPersistence().cacheResult(ddmFormInstanceRecord);
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
	public static void clearCache(DDMFormInstanceRecord ddmFormInstanceRecord) {
		getPersistence().clearCache(ddmFormInstanceRecord);
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
	public static Map<Serializable, DDMFormInstanceRecord> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMFormInstanceRecord> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMFormInstanceRecord> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMFormInstanceRecord> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMFormInstanceRecord update(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		return getPersistence().update(ddmFormInstanceRecord);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMFormInstanceRecord update(
		DDMFormInstanceRecord ddmFormInstanceRecord,
		ServiceContext serviceContext) {

		return getPersistence().update(ddmFormInstanceRecord, serviceContext);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByUuid_First(
			String uuid,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByUuid_First(
		String uuid,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance records where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of ddm form instance records where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm form instance records
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFormInstanceRecordException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByUUID_G(String uuid, long groupId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the ddm form instance record where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm form instance record that was removed
	 */
	public static DDMFormInstanceRecord removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of ddm form instance records where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm form instance records
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance records where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm form instance records
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFormInstanceId(
			formInstanceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByFormInstanceId_First(
			long formInstanceId,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByFormInstanceId_First(
			formInstanceId, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByFormInstanceId_First(
		long formInstanceId,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().fetchByFormInstanceId_First(
			formInstanceId, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance records where formInstanceId = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 */
	public static void removeByFormInstanceId(long formInstanceId) {
		getPersistence().removeByFormInstanceId(formInstanceId);
	}

	/**
	 * Returns the number of ddm form instance records where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance records
	 */
	public static int countByFormInstanceId(long formInstanceId) {
		return getPersistence().countByFormInstanceId(formInstanceId);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_F(
			userId, formInstanceId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByU_F_First(
			long userId, long formInstanceId,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByU_F_First(
			userId, formInstanceId, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByU_F_First(
		long userId, long formInstanceId,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().fetchByU_F_First(
			userId, formInstanceId, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance records where userId = &#63; and formInstanceId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 */
	public static void removeByU_F(long userId, long formInstanceId) {
		getPersistence().removeByU_F(userId, formInstanceId);
	}

	/**
	 * Returns the number of ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance records
	 */
	public static int countByU_F(long userId, long formInstanceId) {
		return getPersistence().countByU_F(userId, formInstanceId);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByF_I(
			formInstanceId, ipAddress, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord findByF_I_First(
			long formInstanceId, String ipAddress,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByF_I_First(
			formInstanceId, ipAddress, orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record in the ordered set where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByF_I_First(
		long formInstanceId, String ipAddress,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().fetchByF_I_First(
			formInstanceId, ipAddress, orderByComparator);
	}

	/**
	 * Removes all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 */
	public static void removeByF_I(long formInstanceId, String ipAddress) {
		getPersistence().removeByF_I(formInstanceId, ipAddress);
	}

	/**
	 * Returns the number of ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @return the number of matching ddm form instance records
	 */
	public static int countByF_I(long formInstanceId, String ipAddress) {
		return getPersistence().countByF_I(formInstanceId, ipAddress);
	}

	/**
	 * Creates a new ddm form instance record with the primary key. Does not add the ddm form instance record to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm form instance record
	 * @return the new ddm form instance record
	 */
	public static DDMFormInstanceRecord create(long formInstanceRecordId) {
		return getPersistence().create(formInstanceRecordId);
	}

	/**
	 * Removes the ddm form instance record with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record that was removed
	 * @throws NoSuchFormInstanceRecordException if a ddm form instance record with the primary key could not be found
	 */
	public static DDMFormInstanceRecord remove(long formInstanceRecordId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().remove(formInstanceRecordId);
	}

	public static DDMFormInstanceRecord updateImpl(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		return getPersistence().updateImpl(ddmFormInstanceRecord);
	}

	/**
	 * Returns the ddm form instance record with the primary key or throws a <code>NoSuchFormInstanceRecordException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record
	 * @throws NoSuchFormInstanceRecordException if a ddm form instance record with the primary key could not be found
	 */
	public static DDMFormInstanceRecord findByPrimaryKey(
			long formInstanceRecordId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchFormInstanceRecordException {

		return getPersistence().findByPrimaryKey(formInstanceRecordId);
	}

	/**
	 * Returns the ddm form instance record with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record, or <code>null</code> if a ddm form instance record with the primary key could not be found
	 */
	public static DDMFormInstanceRecord fetchByPrimaryKey(
		long formInstanceRecordId) {

		return getPersistence().fetchByPrimaryKey(formInstanceRecordId);
	}

	/**
	 * Returns the ddm form instance record where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	public static DDMFormInstanceRecord fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the ddm form instance records where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId) {

		return getPersistence().findByFormInstanceId(formInstanceId);
	}

	/**
	 * Returns a range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end) {

		return getPersistence().findByFormInstanceId(
			formInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByFormInstanceId(
		long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findByFormInstanceId(
			formInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId) {

		return getPersistence().findByU_F(userId, formInstanceId);
	}

	/**
	 * Returns a range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end) {

		return getPersistence().findByU_F(userId, formInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByU_F(
		long userId, long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findByU_F(
			userId, formInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @return the matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress) {

		return getPersistence().findByF_I(formInstanceId, ipAddress);
	}

	/**
	 * Returns a range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end) {

		return getPersistence().findByF_I(
			formInstanceId, ipAddress, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm form instance records where formInstanceId = &#63; and ipAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param ipAddress the ip address
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm form instance records
	 */
	public static List<DDMFormInstanceRecord> findByF_I(
		long formInstanceId, String ipAddress, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator) {

		return getPersistence().findByF_I(
			formInstanceId, ipAddress, start, end, orderByComparator);
	}

	public static DDMFormInstanceRecordPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		DDMFormInstanceRecordPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile DDMFormInstanceRecordPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:121515901